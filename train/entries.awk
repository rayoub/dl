
BEGIN {
    FS = "\t"
    OFS = ","
}
{ 
    split($7, parts, ",")
    if (parts[1] != "NOT") {
        print tolower($1), $3, parts[1]
    }
}
