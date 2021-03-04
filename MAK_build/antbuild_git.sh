#!/bin/bash
#source ~/build_java.cmd
#cvs update &> antbuild.out
#-Dfile.encoding=UTF-8
#cd /usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/MAK_build_git/MAK
git pull
cd ..
ant -verbose -buildfile MAKantbuild_git.xml &> antbuild_git.out
