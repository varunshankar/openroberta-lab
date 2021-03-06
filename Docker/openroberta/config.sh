# essential configuration variables. Be very carefull!

INAME=''                # the (short, alphanumeric) name of this installation
DATABASE_SERVER_PORT='' # one database server for all jetty server. It is listening on this port. Usually this is '9001'
DOCKER_NETWORK_NAME=''  # the network for cooperation between jetty and database server. Usually this is 'ora-net'

SERVERS=''              # these servers can run (used in start-all and stop-all), e.g. 'test dev dev4'
AUTODEPLOY=''           # these servers are deployed when new commits hit the git repo. See auto-deploy and cron, e.g. 'dev dev4'
DATABASES=''            # these databases are served by the database server, e.g. 'test dev dev4'
