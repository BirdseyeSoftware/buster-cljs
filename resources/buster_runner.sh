#!/bin/bash

BUSTER_OUTPUT_FILE=/tmp/buster.log

function exec_in_emacs {
    emacsclient -e "$@" 2>&1> /dev/null
}

function run_buster {
    exec_in_emacs '(dss/autotest-begin-notification)'
    buster_output=$(buster test -r specification -C none -e $1; exit $?)
    buster_exit_code=$?
    # IMPORTANT:
    # given that buster-test no-color option is broken
    # we will add the numbers from the escaping codes
    # in reality the important ones are the last 7 - 1.
    # buster_stats=(0 0 0 0 0 0 0 0 0)
    # buster_stats_failures_index=5
    # buster_stats_error_index=6
    buster_stats=(0 0 0 0 0 0)
    buster_stats_failures_index=3
    buster_stats_error_index=4
    buster_failed=-1
    while read -r line; do
        if [[ $line =~ "ECONNREFUSED" ]] && [[ $1 = browser ]]; then
            exec_in_emacs '(dss/autotest-warning "Start buster-server in order to test browser")'
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
        echo 'do nothing...'
    elif [[ ${buster_stats[$buster_stats_failures_index]} > 0 ]] ||
         [[ ${buster_stats[$buster_stats_error_index]} > 0 ]]; then
        exec_in_emacs '(dss/autotest-fail)'
        exec_in_emacs '(progn (pop-to-buffer "buster.log") (end-of-buffer))'
    else
        exec_in_emacs '(dss/autotest-succeed)'
    fi
    return $buster_exit_code
    #emacsclient -e '(dss/autotest-begin-notification)' 2>&1> /dev/null
    #buster-test -r specification -C none -e $1 >> $BUSTER_OUTPUT_FILE
    #did_fail=$?
    #if $did_fail; then
    #    emacsclient -e '(dss/autotest-succeed)' 2>&1> /dev/null
    #else
    #    emacsclient -e '(dss/autotest-fail)' 2>&1> /dev/null
    #    emacsclient -e "(progn (find-file \"$BUSTER_OUTPUT_FILE\") (end-of-buffer))" 2>&1> /dev/null
    #fi
    #return $did_fail
}

# echo "$@" 2>&1> /tmp/after_cljsbuild.log
# emacsclient -e "(message \"Roman check: $1\")" 2>&1> /dev/null
# sleep 3
# if [[ $1 =~ "dev" ]]; then
#     touch test/cljs/dalap/test/html_test.cljs
# fi
if [[ $1 =~ "browser_test" ]]; then
    run_buster browser
    #emacsclient -e '(dss/autotest-begin-notification)' 2>&1> /dev/null
    #if buster-test -r specification -C none -e browser >> $BUSTER_OUTPUT_FILE; then
    #    emacsclient -e '(dss/autotest-succeed)' 2>&1> /dev/null
    #else
    #    emacsclient -e '(dss/autotest-fail)' 2>&1> /dev/null
    #    emacsclient -e "(progn (find-file \"$BUSTER_OUTPUT_FILE\") (end-of-buffer))" 2>&1> /dev/null
    #fi
elif [[ $1 =~ "node_test" ]]; then
    run_buster node
fi
