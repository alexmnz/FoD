#--- orig files from meld tool
git rm -r *.orig
git rm -r *~

#--- Android files
# built application files
git rm *.apk
git rm *.ap_

# files for the dex VM
git rm *.dex

# Java class files
git rm *.class

# generated files
git rm -r -f bin/
git rm -r gen/

# Local configuration file (sdk path, etc)
git rm local.properties

#--- generic eclipse files
git rm *.pydevproject
git rm .project
git rm .metadata
git rm bin/**
git rm tmp/**
git rm tmp/**/*
git rm *.tmp
git rm *.bak
git rm *.swp
git rm *~.nib
git rm local.properties
git rm .classpath
#git rm -r -f .settings/
git rm .loadpath

# External tool builders
git rm -r .externalToolBuilders/

# Locally stored "Eclipse launch configurations"
git rm *.launch

# CDT-specific
git rm .cproject

# PDT-specific
git rm .buildpath
