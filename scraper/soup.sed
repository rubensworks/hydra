#n
#####
# Tested with GNU sed, run with -r flag.
#####
1,/Vlees\/vis\/vegetarisch/d
/Aanbevolen menu:/,$d
/<tr style/{
	:load
	/€ 0/!{
		N
		b load
	}
	N
	s/.*> *([A-Z][a-z]*dag *)<\/b> *<\/td>[^<]*<td[^>]*>([^<]+)<.*<tr style[^€]*€ ([0-9,]+) *<.*/\1\n\3 - \2/p
}