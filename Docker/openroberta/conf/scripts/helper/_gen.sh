#!/bin/bash

isServerNameValid ${SERVER_NAME}
SERVER_DIR_OF_ONE_SERVER=${SERVER_DIR}/${SERVER_NAME}
isDirectoryValid $SERVER_DIR_OF_ONE_SERVER

source $SERVER_DIR_OF_ONE_SERVER/decl.sh
isDeclShValid

case "${GIT_REPO}" in
    /*) : ;;
    *)  GIT_REPO=${BASE_DIR}/git/${GIT_REPO}
esac
isDirectoryValid ${GIT_REPO}
case "$BRANCH" in
    master) DEBUG=true
            question 'do you really want to generate the docker container for MASTER?'
            if [ "$GIT_UPTODATE" != 'true' ]
            then
               echo 'because we have manual updates before deploying master (piwick, ...), GIT_UPTODATE must be "true". Exit 12'
               exit 12
            fi
            question 'do you have modified the MASTER branch as usual before deployment?' ;;
     *)     : ;;
esac

# ----- AQUIRE A FILE LOCK. Checkout, build binaries, export, build container -----
( flock -w 1200 9 || (echo "$DATE: deployement of ${SERVER_NAME} was delayed for more than 20 minutes. Exit 12"; exit 12) 
    [ "$DEBUG" = 'true' ] && echo "$DATE: got the lock for file '${GIT_DIR}/lockfile'"
    cd ${GIT_REPO}
    if [ "$GIT_UPTODATE" == 'true' ]
    then
        echo "git repo is already uptodate, nothing pulled, nothing checked out"
    elif [ "$COMMIT" == '' ]
    then
        echo "checking out branch $BRANCH and get the actual state"
        git pull
        git checkout -f $BRANCH
        git pull
        LAST_COMMIT=$(git rev-list HEAD...HEAD~1)
    else
        echo "checking out commit $COMMIT"
        git checkout -f $COMMIT
        LAST_COMMIT=$COMMIT
    fi
    mvn clean install -DskipTests
    rm -rf $SERVER_DIR_OF_ONE_SERVER/export
    ./ora.sh export $SERVER_DIR_OF_ONE_SERVER/export gzip
    cp ${CONF_DIR}/docker-for-lab/start.sh $SERVER_DIR_OF_ONE_SERVER/export
    cp ${CONF_DIR}/docker-for-lab/admin.sh $SERVER_DIR_OF_ONE_SERVER/export
    chmod ugo+x $SERVER_DIR_OF_ONE_SERVER/export/*.sh
    IMAGE="rbudde/openroberta_${INAME}_$SERVER_NAME:2"
    DOCKERRM=$(docker rmi $IMAGE 2>/dev/null)
    case "$DOCKERRM" in
        '') echo "found no docker image '$IMAGE' to remove. That is ok." ;;
        * ) echo "removed docker image '$IMAGE'"
    esac
    docker build --no-cache -f ${CONF_DIR}/docker-for-lab/DockerfileLab -t $IMAGE $SERVER_DIR_OF_ONE_SERVER/export
    
    DATE_DEPLOY=$(date --rfc-3339=seconds)
    cat >$SERVER_DIR_OF_ONE_SERVER/deploy.txt <<.EOF
HOSTNAME = $HOSTNAME
DATE_SETUP = $DATE_SETUP
DATE_DEPLOY = $DATE_DEPLOY
GIT_REPO = ${GIT_REPO}
BRANCH = $BRANCH
LAST_COMMIT = $LAST_COMMIT
PORT = $PORT
.EOF

) 9>${GIT_DIR}/lockfile
