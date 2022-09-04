#!/bin/bash
git pull
ant -verbose -buildfile MAKantbuild_git.xml &> antbuild_git.out
