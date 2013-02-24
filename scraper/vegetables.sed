#n
#####
# Tested with GNU sed, run with -r flag.
#####
1,/Vlees\/vis\/vegetarisch/d
/Aanbevolen menu:/,$d
/<tr style/{
	:load
	/\/tr/!{
		N
		b load
	}
	s/.*> *([A-Z][a-z]*dag) *<.*> *([A-Z][^<]+) *<.*> *([A-Z][^<]+) *<.*/\1\n\2\n\3/p
}