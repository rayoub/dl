
BEGIN { 
    OFS = ","
    processed = 0
    model = "none"
}  

$0 ~ /^Template/ { model = $NF }
$0 ~ /^\// && processed == 0 { processed = 1; next }
$0 ~ /^\// && processed == 1 { exit }
$0 !~ /^\// && $0 !~ /^ *Num/ && $0 !~ /^ *$/ && processed == 1 { print dir, model, $1, $2, $3, $4 }


