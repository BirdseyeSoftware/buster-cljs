#!/bin/bash
#
# This bash file is to be used as a `:notify-command' in
# lein-cljsbuild options.
#
# In order for this to work, it will require:
#
# * busterjs (you'll need nodejs and npm)
# * emacs (optional)
# * lein-cljsbuild
#
# In your emacs setup, install via el-get the following packages:
#
# * multi-term (special branch) => https://github.com/roman/multi-term
# * cljsbuild-mode => https://github.com/BirdseyeSoftware/cljsbuild-mode
# * autotest-mode => https://github.com/BirdseyeSoftware/autotest-mode
#
# In your cljsbuild setup:
#
# * an output js with the string browser_test
# * an output js with the string node_test
#
# In your buster.js config file:
#
# * A browser entry that has the resulting browser_test javascript file
# * A node entry that has the resulting node_test javascript file
#
BUSTER_OUTPUT_FILE=/tmp/buster.log

function exec_in_emacs {
    if $(command -v emacsclient 2>&1> /dev/null); then
        emacsclient -e "$@" 2>&1> /dev/null
    fi
}

function notify {
    if $(command -v growlnotify 2>&1> /dev/null); then
        growlnotify -t "$1" -m "$2"
    fi
}

function run_buster {
    exec_in_emacs '(autotest-mode-begin-notification)'
    buster_output=$(buster-test -r specification -C none -e $1 2>&1; exit $?)
    buster_exit_code=$?
    if [[ $1 = browser ]]; then
        buster_stats=(0 0 0 0 0 0)
        buster_stats_failures_index=3
        buster_stats_error_index=4
        buster_failed=-1
    elif [[ $1 = node ]]; then
        # IMPORTANT:
        # given that buster-test no-color option is broken
        # on node we will add the numbers from the escaping codes
        # in reality the important ones are the last 7 - 1.
        buster_stats=(0 0 0 0 0 0 0 0 0)
        buster_stats_failures_index=8
        buster_stats_error_index=9
    fi
    while read -r line; do
        if [[ $line =~ "Unable to connect to server" ]] && [[ $1 = browser ]]; then
            exec_in_emacs '(autotest-mode-warning "Start buster-server in order to test browser")'
            exec_in_emacs '(sit-for 1)'
            buster_failed=1
            break
        elif [[ $line =~ "test cases" ]]; then
            buster_stats=($(echo "$line"                 |
                            grep -Ei '([0-9]+ [a-z]+,?)' |
                            grep -oEi '[0-9]+'))
            break
        fi
    done <<< "$buster_output"
    echo "$buster_output" >> $BUSTER_OUTPUT_FILE
    if [[ $buster_failed -eq 1 ]]; then
        notify "busterjs" "test suite for $1 failed"
    elif [[ ${buster_stats[$buster_stats_failures_index]} > 0 ]] ||
         [[ ${buster_stats[$buster_stats_error_index]} > 0 ]]; then
        notify "busterjs" "test suite for $1 failed"
        exec_in_emacs '(autotest-mode-fail)'
        exec_in_emacs "(progn
                         (save-window-excursion
                           (find-file \"$BUSTER_OUTPUT_FILE\"))
                         (pop-to-buffer \"buster.log\")
                         (end-of-buffer))"
    else
        notify "busterjs" "test suite for $1 passed"
        exec_in_emacs '(autotest-mode-succeed)'
    fi
    return $buster_exit_code
}

if [[ $1 =~ "browser_test" ]]; then
    run_buster browser
elif [[ $1 =~ "node_test" ]]; then
    run_buster node
fi
