#####
# Tested with GNU sed, run with -r flag.
#####
s/(.*)underline(.*)€/\1\2R €/g
s/<[^u][^>]*>//g
s/€ ([0-9],[0-9][0-9]) +\- +([A-Z])/EUR \1 - \2/g
1,/Vlees\/vis\/vegetarisch/d
/Aanbevolen menu:/,$d
/EUR|[a-z]dag/!d
s/(.*)<u>(.*)EUR/\1\2R EUR/g
s/^(.+[^R] )EUR(.+)$/\1\nEUR\2/g
s/^(.+[^ ])EUR(.+)$/\1\nEUR\2/g
s/^(.+)R EUR(.+)$/\1\nEUR\2/g
s/EUR *//g