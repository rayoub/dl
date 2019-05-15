#! /bin/bash

dir=$1

find ./${dir} -mindepth 1 -name '*.tpl' -printf '%p\n' | xargs -L1 awk -v dir=${dir} -f ./parser.awk

