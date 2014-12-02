#!/bin/bash
#
# Command line statements which use the RVF API to test a simple refset
#
# Stop on error
set -e;
#set -x;  #echo every statement executed
#
# Declare parameters
fileToTest="rel2_Refset_SimpleDelta_INT_20140131.txt"

# Target API Deployment
#TODO - allow the user to change the API at runtime
#api="http://localhost:8080/api/v1"
#api="http://localhost:8081/api/v1"
api="https://dev-rvf.ihtsdotools.org/api/v1"
#api="https://uat-rvf.ihtsdotools.org/api/v1"

#TODO make this function miss out the data if jsonFile is not specified.
function callURL() {
	httpMethod=$1
	url=$2
	jsonFile=$3
	dataArg=""
	if [ -n "${jsonFile}" ] 
	then
		jsonData=`cat ${jsonFile}`   #Parsing the json to objects in spring seems very forgiving of white space and unescaped characters.
		dataArg="${jsonData}"
	fi
	curl -i \
	--header "Content-type: application/json" \
	--header "Accept: application/json" \
	-X ${httpMethod} \
	-d "${dataArg}" \
	${url}
}

function getReleaseDate() {
	releaseDate=`echo $1 | sed 's/[^0-9]//g'`
	if [ -z $releaseDate ] 
	then
		echo "Failed to find release date in $1.\nScript halting"
		exit -1
	fi
	echo $releaseDate
} 

function listKnownReleases() {
	echo "Listing Known Releases:"	
	callURL GET ${api}/releases
}

function uploadRelease() {
	read -p "What file should be uploaded?: " releaseFile
	releaseDate=`getReleaseDate ${releaseFile}`
	url=" ${api}/releases/${releaseDate}"
	echo "Uploading release file to ${url}"
	curl -X POST -F file=@${releaseFile} ${url} 
}


function mainMenu() {
	echo 
	echo "*****   RVF Menu    ******"
	echo "l - List known previous releases"
	echo "u - Upload a previous release"
	echo "q - quit"
	echo
	while :
	do
		read -s -n 1 user_choice
		case "$user_choice" in
			l|L) listKnownReleases ; break;;
			u|U) uploadRelease ; break;;
			q|Q) echo "Quitting..."; exit 0;;
		*) echo -e 'Option not recognised\n';;
		esac
	done
}

echo
echo "Target Release Validation Framework API URL is '${api}'"
echo

while true
do
	mainMenu
done

echo "Program exited unexpectedly"