#
# Endeca Environment Variables
#

# Application/deployment variables

# ENDECA_PROJECT_DIR specifies the path of the deployed application
# e.g. ENDECA_PROJECT_DIR=/localdisk/apps/myapp
# obtain a reference to the running script
EXECUTING_SCRIPT=${0}
# all scripts are assumed to run from /control, so project dir is one up
ENDECA_PROJECT_DIR=`dirname ${EXECUTING_SCRIPT}`/..
# make sure that this is an absolute path
ABS_PATH=`(cd $ENDECA_PROJECT_DIR 2>/dev/null && pwd ;)`
ENDECA_PROJECT_DIR=$ABS_PATH
export ENDECA_PROJECT_DIR

# ENDECA_PROJECT_NAME specifies the project name that will be used, for
# example, as the JCD job prefix for jobs defined in the project's
# Job Control Daemon (JCD).
# e.g. ENDECA_PROJECT_NAME=myapp
ENDECA_PROJECT_NAME=@APP_NAME@
export ENDECA_PROJECT_NAME


# Endeca software variables

# These variables specify the location of the Endeca software on this system
# and add Endeca-specific paths to the Perl, path and Java environment variables.

# All variables are set relative to the ENDECA_ROOT environment variable. Variables
# should be updated to reflect the location of your Endeca install. Instructions
# can be found in the core product documentation. You may also retrieve these
# variable settings from the installer_sh.ini or installer_csh.ini file
# (e.g. /usr/endeca/4.7.4/i86pc-linux/setup/installer_sh.ini)

# ENDECA_ROOT specifies the root directory of the installed Endeca software
# e.g. ENDECA_ROOT=/usr/endeca/4.7.4/i86pc-linux
ENDECA_ROOT=@ENDECA_BASE_DIR@/PlatformServices/11.2.0
export ENDECA_ROOT

PERLLIB=$ENDECA_ROOT/lib/perl:$ENDECA_ROOT/lib/perl/Control:$ENDECA_ROOT/perl/lib:$ENDECA_ROOT/perl/lib/site_perl:$PERLLIB
export PERLLIB

PERL5LIB=$ENDECA_ROOT/lib/perl:$ENDECA_ROOT/lib/perl/Control:$ENDECA_ROOT/perl/lib:$ENDECA_ROOT/perl/lib/site_perl:$PERL5LIB
export PERL5LIB

PATH=$ENDECA_ROOT/bin:$ENDECA_ROOT/perl/bin:$ENDECA_ROOT/tools/server/bin:$ENDECA_ROOT/utilities:$PATH
export PATH

JAVA_HOME=$ENDECA_ROOT/j2sdk:$JAVA_HOME
export JAVA_HOME
