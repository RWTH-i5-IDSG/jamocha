(deftemplate transaction
  (slot accountId)
  (slot buyPrice)
  (slot countryCode)
  (slot currentPrice)
  (slot cusip)
  (slot exchange)
  (slot fitchLongRating)
  (slot fitchShortRating)
  (slot gaurantor)
  (slot industryGroupID)
  (slot industryID)
  (slot issuer)
  (slot lastPrice)
  (slot purchaseDate)
  (slot sectorID)
  (slot shares)
  (slot spLongRating)
  (slot spShortRating)
  (slot subIndustryID)
)
(defrule rule0
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule0 was fired" )
)
(defrule rule1
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1 was fired" )
)
(defrule rule2
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule2 was fired" )
)
(defrule rule3
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule3 was fired" )
)
(defrule rule4
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule4 was fired" )
)
(defrule rule5
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule5 was fired" )
)
(defrule rule6
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule6 was fired" )
)
(defrule rule7
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule7 was fired" )
)
(defrule rule8
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule8 was fired" )
)
(defrule rule9
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule9 was fired" )
)
(defrule rule10
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule10 was fired" )
)
(defrule rule11
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule11 was fired" )
)
(defrule rule12
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule12 was fired" )
)
(defrule rule13
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule13 was fired" )
)
(defrule rule14
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule14 was fired" )
)
(defrule rule15
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule15 was fired" )
)
(defrule rule16
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule16 was fired" )
)
(defrule rule17
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule17 was fired" )
)
(defrule rule18
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule18 was fired" )
)
(defrule rule19
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule19 was fired" )
)
(defrule rule20
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule20 was fired" )
)
(defrule rule21
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule21 was fired" )
)
(defrule rule22
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule22 was fired" )
)
(defrule rule23
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule23 was fired" )
)
(defrule rule24
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule24 was fired" )
)
(defrule rule25
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule25 was fired" )
)
(defrule rule26
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule26 was fired" )
)
(defrule rule27
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule27 was fired" )
)
(defrule rule28
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule28 was fired" )
)
(defrule rule29
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule29 was fired" )
)
(defrule rule30
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule30 was fired" )
)
(defrule rule31
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule31 was fired" )
)
(defrule rule32
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule32 was fired" )
)
(defrule rule33
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule33 was fired" )
)
(defrule rule34
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule34 was fired" )
)
(defrule rule35
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule35 was fired" )
)
(defrule rule36
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule36 was fired" )
)
(defrule rule37
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule37 was fired" )
)
(defrule rule38
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule38 was fired" )
)
(defrule rule39
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule39 was fired" )
)
(defrule rule40
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule40 was fired" )
)
(defrule rule41
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule41 was fired" )
)
(defrule rule42
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule42 was fired" )
)
(defrule rule43
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule43 was fired" )
)
(defrule rule44
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule44 was fired" )
)
(defrule rule45
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule45 was fired" )
)
(defrule rule46
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule46 was fired" )
)
(defrule rule47
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule47 was fired" )
)
(defrule rule48
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule48 was fired" )
)
(defrule rule49
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule49 was fired" )
)
(defrule rule50
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule50 was fired" )
)
(defrule rule51
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule51 was fired" )
)
(defrule rule52
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule52 was fired" )
)
(defrule rule53
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule53 was fired" )
)
(defrule rule54
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule54 was fired" )
)
(defrule rule55
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule55 was fired" )
)
(defrule rule56
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule56 was fired" )
)
(defrule rule57
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule57 was fired" )
)
(defrule rule58
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule58 was fired" )
)
(defrule rule59
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule59 was fired" )
)
(defrule rule60
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule60 was fired" )
)
(defrule rule61
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule61 was fired" )
)
(defrule rule62
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule62 was fired" )
)
(defrule rule63
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule63 was fired" )
)
(defrule rule64
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule64 was fired" )
)
(defrule rule65
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule65 was fired" )
)
(defrule rule66
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule66 was fired" )
)
(defrule rule67
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule67 was fired" )
)
(defrule rule68
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule68 was fired" )
)
(defrule rule69
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule69 was fired" )
)
(defrule rule70
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule70 was fired" )
)
(defrule rule71
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule71 was fired" )
)
(defrule rule72
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule72 was fired" )
)
(defrule rule73
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule73 was fired" )
)
(defrule rule74
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule74 was fired" )
)
(defrule rule75
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule75 was fired" )
)
(defrule rule76
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule76 was fired" )
)
(defrule rule77
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule77 was fired" )
)
(defrule rule78
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule78 was fired" )
)
(defrule rule79
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule79 was fired" )
)
(defrule rule80
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101040)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule80 was fired" )
)
(defrule rule81
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule81 was fired" )
)
(defrule rule82
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule82 was fired" )
)
(defrule rule83
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule83 was fired" )
)
(defrule rule84
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule84 was fired" )
)
(defrule rule85
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule85 was fired" )
)
(defrule rule86
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule86 was fired" )
)
(defrule rule87
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule87 was fired" )
)
(defrule rule88
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule88 was fired" )
)
(defrule rule89
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule89 was fired" )
)
(defrule rule90
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule90 was fired" )
)
(defrule rule91
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule91 was fired" )
)
(defrule rule92
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule92 was fired" )
)
(defrule rule93
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule93 was fired" )
)
(defrule rule94
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule94 was fired" )
)
(defrule rule95
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule95 was fired" )
)
(defrule rule96
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule96 was fired" )
)
(defrule rule97
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule97 was fired" )
)
(defrule rule98
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule98 was fired" )
)
(defrule rule99
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule99 was fired" )
)
(defrule rule100
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule100 was fired" )
)
(defrule rule101
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule101 was fired" )
)
(defrule rule102
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule102 was fired" )
)
(defrule rule103
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule103 was fired" )
)
(defrule rule104
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule104 was fired" )
)
(defrule rule105
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule105 was fired" )
)
(defrule rule106
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule106 was fired" )
)
(defrule rule107
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule107 was fired" )
)
(defrule rule108
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule108 was fired" )
)
(defrule rule109
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule109 was fired" )
)
(defrule rule110
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule110 was fired" )
)
(defrule rule111
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule111 was fired" )
)
(defrule rule112
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule112 was fired" )
)
(defrule rule113
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule113 was fired" )
)
(defrule rule114
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule114 was fired" )
)
(defrule rule115
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule115 was fired" )
)
(defrule rule116
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule116 was fired" )
)
(defrule rule117
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule117 was fired" )
)
(defrule rule118
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule118 was fired" )
)
(defrule rule119
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule119 was fired" )
)
(defrule rule120
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule120 was fired" )
)
(defrule rule121
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule121 was fired" )
)
(defrule rule122
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule122 was fired" )
)
(defrule rule123
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule123 was fired" )
)
(defrule rule124
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule124 was fired" )
)
(defrule rule125
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule125 was fired" )
)
(defrule rule126
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule126 was fired" )
)
(defrule rule127
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule127 was fired" )
)
(defrule rule128
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule128 was fired" )
)
(defrule rule129
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule129 was fired" )
)
(defrule rule130
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule130 was fired" )
)
(defrule rule131
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule131 was fired" )
)
(defrule rule132
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule132 was fired" )
)
(defrule rule133
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule133 was fired" )
)
(defrule rule134
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule134 was fired" )
)
(defrule rule135
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule135 was fired" )
)
(defrule rule136
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule136 was fired" )
)
(defrule rule137
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule137 was fired" )
)
(defrule rule138
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule138 was fired" )
)
(defrule rule139
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule139 was fired" )
)
(defrule rule140
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule140 was fired" )
)
(defrule rule141
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule141 was fired" )
)
(defrule rule142
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule142 was fired" )
)
(defrule rule143
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule143 was fired" )
)
(defrule rule144
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule144 was fired" )
)
(defrule rule145
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule145 was fired" )
)
(defrule rule146
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule146 was fired" )
)
(defrule rule147
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule147 was fired" )
)
(defrule rule148
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule148 was fired" )
)
(defrule rule149
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule149 was fired" )
)
(defrule rule150
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule150 was fired" )
)
(defrule rule151
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule151 was fired" )
)
(defrule rule152
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule152 was fired" )
)
(defrule rule153
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule153 was fired" )
)
(defrule rule154
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule154 was fired" )
)
(defrule rule155
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule155 was fired" )
)
(defrule rule156
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule156 was fired" )
)
(defrule rule157
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule157 was fired" )
)
(defrule rule158
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule158 was fired" )
)
(defrule rule159
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule159 was fired" )
)
(defrule rule160
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule160 was fired" )
)
(defrule rule161
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule161 was fired" )
)
(defrule rule162
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule162 was fired" )
)
(defrule rule163
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule163 was fired" )
)
(defrule rule164
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule164 was fired" )
)
(defrule rule165
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule165 was fired" )
)
(defrule rule166
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule166 was fired" )
)
(defrule rule167
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule167 was fired" )
)
(defrule rule168
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule168 was fired" )
)
(defrule rule169
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule169 was fired" )
)
(defrule rule170
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule170 was fired" )
)
(defrule rule171
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule171 was fired" )
)
(defrule rule172
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule172 was fired" )
)
(defrule rule173
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule173 was fired" )
)
(defrule rule174
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule174 was fired" )
)
(defrule rule175
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule175 was fired" )
)
(defrule rule176
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule176 was fired" )
)
(defrule rule177
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule177 was fired" )
)
(defrule rule178
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule178 was fired" )
)
(defrule rule179
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule179 was fired" )
)
(defrule rule180
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule180 was fired" )
)
(defrule rule181
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule181 was fired" )
)
(defrule rule182
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule182 was fired" )
)
(defrule rule183
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule183 was fired" )
)
(defrule rule184
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule184 was fired" )
)
(defrule rule185
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule185 was fired" )
)
(defrule rule186
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule186 was fired" )
)
(defrule rule187
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule187 was fired" )
)
(defrule rule188
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule188 was fired" )
)
(defrule rule189
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule189 was fired" )
)
(defrule rule190
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule190 was fired" )
)
(defrule rule191
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule191 was fired" )
)
(defrule rule192
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule192 was fired" )
)
(defrule rule193
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule193 was fired" )
)
(defrule rule194
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule194 was fired" )
)
(defrule rule195
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule195 was fired" )
)
(defrule rule196
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule196 was fired" )
)
(defrule rule197
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule197 was fired" )
)
(defrule rule198
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule198 was fired" )
)
(defrule rule199
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule199 was fired" )
)
(defrule rule200
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule200 was fired" )
)
(defrule rule201
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule201 was fired" )
)
(defrule rule202
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule202 was fired" )
)
(defrule rule203
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule203 was fired" )
)
(defrule rule204
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule204 was fired" )
)
(defrule rule205
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule205 was fired" )
)
(defrule rule206
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule206 was fired" )
)
(defrule rule207
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule207 was fired" )
)
(defrule rule208
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule208 was fired" )
)
(defrule rule209
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule209 was fired" )
)
(defrule rule210
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule210 was fired" )
)
(defrule rule211
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule211 was fired" )
)
(defrule rule212
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule212 was fired" )
)
(defrule rule213
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule213 was fired" )
)
(defrule rule214
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule214 was fired" )
)
(defrule rule215
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule215 was fired" )
)
(defrule rule216
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule216 was fired" )
)
(defrule rule217
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule217 was fired" )
)
(defrule rule218
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule218 was fired" )
)
(defrule rule219
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule219 was fired" )
)
(defrule rule220
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule220 was fired" )
)
(defrule rule221
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule221 was fired" )
)
(defrule rule222
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule222 was fired" )
)
(defrule rule223
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule223 was fired" )
)
(defrule rule224
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule224 was fired" )
)
(defrule rule225
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule225 was fired" )
)
(defrule rule226
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule226 was fired" )
)
(defrule rule227
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule227 was fired" )
)
(defrule rule228
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule228 was fired" )
)
(defrule rule229
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule229 was fired" )
)
(defrule rule230
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule230 was fired" )
)
(defrule rule231
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule231 was fired" )
)
(defrule rule232
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule232 was fired" )
)
(defrule rule233
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule233 was fired" )
)
(defrule rule234
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule234 was fired" )
)
(defrule rule235
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule235 was fired" )
)
(defrule rule236
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule236 was fired" )
)
(defrule rule237
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule237 was fired" )
)
(defrule rule238
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule238 was fired" )
)
(defrule rule239
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule239 was fired" )
)
(defrule rule240
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule240 was fired" )
)
(defrule rule241
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule241 was fired" )
)
(defrule rule242
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule242 was fired" )
)
(defrule rule243
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule243 was fired" )
)
(defrule rule244
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule244 was fired" )
)
(defrule rule245
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule245 was fired" )
)
(defrule rule246
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule246 was fired" )
)
(defrule rule247
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule247 was fired" )
)
(defrule rule248
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule248 was fired" )
)
(defrule rule249
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule249 was fired" )
)
(defrule rule250
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule250 was fired" )
)
(defrule rule251
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule251 was fired" )
)
(defrule rule252
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule252 was fired" )
)
(defrule rule253
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule253 was fired" )
)
(defrule rule254
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule254 was fired" )
)
(defrule rule255
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule255 was fired" )
)
(defrule rule256
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule256 was fired" )
)
(defrule rule257
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule257 was fired" )
)
(defrule rule258
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule258 was fired" )
)
(defrule rule259
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule259 was fired" )
)
(defrule rule260
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule260 was fired" )
)
(defrule rule261
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule261 was fired" )
)
(defrule rule262
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule262 was fired" )
)
(defrule rule263
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule263 was fired" )
)
(defrule rule264
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule264 was fired" )
)
(defrule rule265
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule265 was fired" )
)
(defrule rule266
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule266 was fired" )
)
(defrule rule267
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule267 was fired" )
)
(defrule rule268
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule268 was fired" )
)
(defrule rule269
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule269 was fired" )
)
(defrule rule270
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule270 was fired" )
)
(defrule rule271
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule271 was fired" )
)
(defrule rule272
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule272 was fired" )
)
(defrule rule273
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule273 was fired" )
)
(defrule rule274
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule274 was fired" )
)
(defrule rule275
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule275 was fired" )
)
(defrule rule276
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule276 was fired" )
)
(defrule rule277
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule277 was fired" )
)
(defrule rule278
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule278 was fired" )
)
(defrule rule279
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule279 was fired" )
)
(defrule rule280
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule280 was fired" )
)
(defrule rule281
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule281 was fired" )
)
(defrule rule282
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule282 was fired" )
)
(defrule rule283
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule283 was fired" )
)
(defrule rule284
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule284 was fired" )
)
(defrule rule285
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule285 was fired" )
)
(defrule rule286
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule286 was fired" )
)
(defrule rule287
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule287 was fired" )
)
(defrule rule288
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule288 was fired" )
)
(defrule rule289
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule289 was fired" )
)
(defrule rule290
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule290 was fired" )
)
(defrule rule291
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule291 was fired" )
)
(defrule rule292
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule292 was fired" )
)
(defrule rule293
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule293 was fired" )
)
(defrule rule294
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule294 was fired" )
)
(defrule rule295
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule295 was fired" )
)
(defrule rule296
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule296 was fired" )
)
(defrule rule297
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule297 was fired" )
)
(defrule rule298
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule298 was fired" )
)
(defrule rule299
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule299 was fired" )
)
(defrule rule300
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule300 was fired" )
)
(defrule rule301
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule301 was fired" )
)
(defrule rule302
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule302 was fired" )
)
(defrule rule303
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule303 was fired" )
)
(defrule rule304
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule304 was fired" )
)
(defrule rule305
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule305 was fired" )
)
(defrule rule306
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule306 was fired" )
)
(defrule rule307
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule307 was fired" )
)
(defrule rule308
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule308 was fired" )
)
(defrule rule309
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule309 was fired" )
)
(defrule rule310
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule310 was fired" )
)
(defrule rule311
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule311 was fired" )
)
(defrule rule312
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule312 was fired" )
)
(defrule rule313
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule313 was fired" )
)
(defrule rule314
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule314 was fired" )
)
(defrule rule315
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule315 was fired" )
)
(defrule rule316
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule316 was fired" )
)
(defrule rule317
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule317 was fired" )
)
(defrule rule318
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule318 was fired" )
)
(defrule rule319
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule319 was fired" )
)
(defrule rule320
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule320 was fired" )
)
(defrule rule321
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule321 was fired" )
)
(defrule rule322
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule322 was fired" )
)
(defrule rule323
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule323 was fired" )
)
(defrule rule324
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule324 was fired" )
)
(defrule rule325
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule325 was fired" )
)
(defrule rule326
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule326 was fired" )
)
(defrule rule327
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule327 was fired" )
)
(defrule rule328
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule328 was fired" )
)
(defrule rule329
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule329 was fired" )
)
(defrule rule330
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule330 was fired" )
)
(defrule rule331
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule331 was fired" )
)
(defrule rule332
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule332 was fired" )
)
(defrule rule333
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule333 was fired" )
)
(defrule rule334
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule334 was fired" )
)
(defrule rule335
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule335 was fired" )
)
(defrule rule336
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule336 was fired" )
)
(defrule rule337
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule337 was fired" )
)
(defrule rule338
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule338 was fired" )
)
(defrule rule339
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule339 was fired" )
)
(defrule rule340
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule340 was fired" )
)
(defrule rule341
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule341 was fired" )
)
(defrule rule342
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule342 was fired" )
)
(defrule rule343
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule343 was fired" )
)
(defrule rule344
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule344 was fired" )
)
(defrule rule345
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule345 was fired" )
)
(defrule rule346
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule346 was fired" )
)
(defrule rule347
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule347 was fired" )
)
(defrule rule348
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule348 was fired" )
)
(defrule rule349
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule349 was fired" )
)
(defrule rule350
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule350 was fired" )
)
(defrule rule351
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule351 was fired" )
)
(defrule rule352
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule352 was fired" )
)
(defrule rule353
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule353 was fired" )
)
(defrule rule354
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule354 was fired" )
)
(defrule rule355
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule355 was fired" )
)
(defrule rule356
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule356 was fired" )
)
(defrule rule357
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule357 was fired" )
)
(defrule rule358
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule358 was fired" )
)
(defrule rule359
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule359 was fired" )
)
(defrule rule360
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule360 was fired" )
)
(defrule rule361
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule361 was fired" )
)
(defrule rule362
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule362 was fired" )
)
(defrule rule363
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule363 was fired" )
)
(defrule rule364
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule364 was fired" )
)
(defrule rule365
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule365 was fired" )
)
(defrule rule366
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule366 was fired" )
)
(defrule rule367
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule367 was fired" )
)
(defrule rule368
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule368 was fired" )
)
(defrule rule369
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule369 was fired" )
)
(defrule rule370
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule370 was fired" )
)
(defrule rule371
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule371 was fired" )
)
(defrule rule372
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule372 was fired" )
)
(defrule rule373
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule373 was fired" )
)
(defrule rule374
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule374 was fired" )
)
(defrule rule375
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule375 was fired" )
)
(defrule rule376
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule376 was fired" )
)
(defrule rule377
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule377 was fired" )
)
(defrule rule378
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule378 was fired" )
)
(defrule rule379
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule379 was fired" )
)
(defrule rule380
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule380 was fired" )
)
(defrule rule381
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule381 was fired" )
)
(defrule rule382
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule382 was fired" )
)
(defrule rule383
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule383 was fired" )
)
(defrule rule384
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule384 was fired" )
)
(defrule rule385
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule385 was fired" )
)
(defrule rule386
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule386 was fired" )
)
(defrule rule387
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule387 was fired" )
)
(defrule rule388
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule388 was fired" )
)
(defrule rule389
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule389 was fired" )
)
(defrule rule390
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule390 was fired" )
)
(defrule rule391
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule391 was fired" )
)
(defrule rule392
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule392 was fired" )
)
(defrule rule393
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule393 was fired" )
)
(defrule rule394
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule394 was fired" )
)
(defrule rule395
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule395 was fired" )
)
(defrule rule396
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule396 was fired" )
)
(defrule rule397
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule397 was fired" )
)
(defrule rule398
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule398 was fired" )
)
(defrule rule399
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule399 was fired" )
)
(defrule rule400
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule400 was fired" )
)
(defrule rule401
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule401 was fired" )
)
(defrule rule402
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule402 was fired" )
)
(defrule rule403
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule403 was fired" )
)
(defrule rule404
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule404 was fired" )
)
(defrule rule405
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule405 was fired" )
)
(defrule rule406
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule406 was fired" )
)
(defrule rule407
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule407 was fired" )
)
(defrule rule408
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule408 was fired" )
)
(defrule rule409
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule409 was fired" )
)
(defrule rule410
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule410 was fired" )
)
(defrule rule411
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule411 was fired" )
)
(defrule rule412
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule412 was fired" )
)
(defrule rule413
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule413 was fired" )
)
(defrule rule414
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule414 was fired" )
)
(defrule rule415
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule415 was fired" )
)
(defrule rule416
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule416 was fired" )
)
(defrule rule417
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule417 was fired" )
)
(defrule rule418
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule418 was fired" )
)
(defrule rule419
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule419 was fired" )
)
(defrule rule420
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule420 was fired" )
)
(defrule rule421
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule421 was fired" )
)
(defrule rule422
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule422 was fired" )
)
(defrule rule423
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule423 was fired" )
)
(defrule rule424
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule424 was fired" )
)
(defrule rule425
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule425 was fired" )
)
(defrule rule426
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule426 was fired" )
)
(defrule rule427
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule427 was fired" )
)
(defrule rule428
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule428 was fired" )
)
(defrule rule429
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule429 was fired" )
)
(defrule rule430
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule430 was fired" )
)
(defrule rule431
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule431 was fired" )
)
(defrule rule432
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule432 was fired" )
)
(defrule rule433
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule433 was fired" )
)
(defrule rule434
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule434 was fired" )
)
(defrule rule435
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule435 was fired" )
)
(defrule rule436
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule436 was fired" )
)
(defrule rule437
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule437 was fired" )
)
(defrule rule438
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule438 was fired" )
)
(defrule rule439
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule439 was fired" )
)
(defrule rule440
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule440 was fired" )
)
(defrule rule441
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule441 was fired" )
)
(defrule rule442
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule442 was fired" )
)
(defrule rule443
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule443 was fired" )
)
(defrule rule444
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule444 was fired" )
)
(defrule rule445
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule445 was fired" )
)
(defrule rule446
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule446 was fired" )
)
(defrule rule447
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule447 was fired" )
)
(defrule rule448
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule448 was fired" )
)
(defrule rule449
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule449 was fired" )
)
(defrule rule450
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule450 was fired" )
)
(defrule rule451
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule451 was fired" )
)
(defrule rule452
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule452 was fired" )
)
(defrule rule453
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule453 was fired" )
)
(defrule rule454
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule454 was fired" )
)
(defrule rule455
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule455 was fired" )
)
(defrule rule456
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule456 was fired" )
)
(defrule rule457
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule457 was fired" )
)
(defrule rule458
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule458 was fired" )
)
(defrule rule459
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule459 was fired" )
)
(defrule rule460
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule460 was fired" )
)
(defrule rule461
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule461 was fired" )
)
(defrule rule462
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule462 was fired" )
)
(defrule rule463
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule463 was fired" )
)
(defrule rule464
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule464 was fired" )
)
(defrule rule465
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule465 was fired" )
)
(defrule rule466
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule466 was fired" )
)
(defrule rule467
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule467 was fired" )
)
(defrule rule468
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule468 was fired" )
)
(defrule rule469
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule469 was fired" )
)
(defrule rule470
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule470 was fired" )
)
(defrule rule471
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule471 was fired" )
)
(defrule rule472
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule472 was fired" )
)
(defrule rule473
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule473 was fired" )
)
(defrule rule474
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule474 was fired" )
)
(defrule rule475
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule475 was fired" )
)
(defrule rule476
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule476 was fired" )
)
(defrule rule477
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule477 was fired" )
)
(defrule rule478
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule478 was fired" )
)
(defrule rule479
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule479 was fired" )
)
(defrule rule480
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule480 was fired" )
)
(defrule rule481
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule481 was fired" )
)
(defrule rule482
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule482 was fired" )
)
(defrule rule483
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule483 was fired" )
)
(defrule rule484
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule484 was fired" )
)
(defrule rule485
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule485 was fired" )
)
(defrule rule486
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule486 was fired" )
)
(defrule rule487
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule487 was fired" )
)
(defrule rule488
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule488 was fired" )
)
(defrule rule489
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule489 was fired" )
)
(defrule rule490
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule490 was fired" )
)
(defrule rule491
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule491 was fired" )
)
(defrule rule492
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule492 was fired" )
)
(defrule rule493
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule493 was fired" )
)
(defrule rule494
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule494 was fired" )
)
(defrule rule495
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule495 was fired" )
)
(defrule rule496
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule496 was fired" )
)
(defrule rule497
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule497 was fired" )
)
(defrule rule498
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule498 was fired" )
)
(defrule rule499
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule499 was fired" )
)
(defrule rule500
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule500 was fired" )
)
(defrule rule501
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule501 was fired" )
)
(defrule rule502
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule502 was fired" )
)
(defrule rule503
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule503 was fired" )
)
(defrule rule504
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule504 was fired" )
)
(defrule rule505
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule505 was fired" )
)
(defrule rule506
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule506 was fired" )
)
(defrule rule507
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule507 was fired" )
)
(defrule rule508
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule508 was fired" )
)
(defrule rule509
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule509 was fired" )
)
(defrule rule510
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule510 was fired" )
)
(defrule rule511
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule511 was fired" )
)
(defrule rule512
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule512 was fired" )
)
(defrule rule513
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule513 was fired" )
)
(defrule rule514
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule514 was fired" )
)
(defrule rule515
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule515 was fired" )
)
(defrule rule516
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule516 was fired" )
)
(defrule rule517
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule517 was fired" )
)
(defrule rule518
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule518 was fired" )
)
(defrule rule519
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule519 was fired" )
)
(defrule rule520
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule520 was fired" )
)
(defrule rule521
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule521 was fired" )
)
(defrule rule522
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule522 was fired" )
)
(defrule rule523
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule523 was fired" )
)
(defrule rule524
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule524 was fired" )
)
(defrule rule525
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule525 was fired" )
)
(defrule rule526
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule526 was fired" )
)
(defrule rule527
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule527 was fired" )
)
(defrule rule528
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule528 was fired" )
)
(defrule rule529
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule529 was fired" )
)
(defrule rule530
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule530 was fired" )
)
(defrule rule531
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule531 was fired" )
)
(defrule rule532
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule532 was fired" )
)
(defrule rule533
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule533 was fired" )
)
(defrule rule534
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule534 was fired" )
)
(defrule rule535
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule535 was fired" )
)
(defrule rule536
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule536 was fired" )
)
(defrule rule537
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule537 was fired" )
)
(defrule rule538
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule538 was fired" )
)
(defrule rule539
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule539 was fired" )
)
(defrule rule540
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule540 was fired" )
)
(defrule rule541
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule541 was fired" )
)
(defrule rule542
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule542 was fired" )
)
(defrule rule543
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule543 was fired" )
)
(defrule rule544
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule544 was fired" )
)
(defrule rule545
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule545 was fired" )
)
(defrule rule546
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule546 was fired" )
)
(defrule rule547
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule547 was fired" )
)
(defrule rule548
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule548 was fired" )
)
(defrule rule549
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule549 was fired" )
)
(defrule rule550
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule550 was fired" )
)
(defrule rule551
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule551 was fired" )
)
(defrule rule552
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule552 was fired" )
)
(defrule rule553
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule553 was fired" )
)
(defrule rule554
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule554 was fired" )
)
(defrule rule555
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule555 was fired" )
)
(defrule rule556
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule556 was fired" )
)
(defrule rule557
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule557 was fired" )
)
(defrule rule558
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule558 was fired" )
)
(defrule rule559
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule559 was fired" )
)
(defrule rule560
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule560 was fired" )
)
(defrule rule561
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule561 was fired" )
)
(defrule rule562
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule562 was fired" )
)
(defrule rule563
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule563 was fired" )
)
(defrule rule564
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule564 was fired" )
)
(defrule rule565
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule565 was fired" )
)
(defrule rule566
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule566 was fired" )
)
(defrule rule567
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule567 was fired" )
)
(defrule rule568
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule568 was fired" )
)
(defrule rule569
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule569 was fired" )
)
(defrule rule570
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule570 was fired" )
)
(defrule rule571
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule571 was fired" )
)
(defrule rule572
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule572 was fired" )
)
(defrule rule573
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule573 was fired" )
)
(defrule rule574
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule574 was fired" )
)
(defrule rule575
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule575 was fired" )
)
(defrule rule576
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule576 was fired" )
)
(defrule rule577
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule577 was fired" )
)
(defrule rule578
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule578 was fired" )
)
(defrule rule579
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule579 was fired" )
)
(defrule rule580
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule580 was fired" )
)
(defrule rule581
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule581 was fired" )
)
(defrule rule582
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule582 was fired" )
)
(defrule rule583
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule583 was fired" )
)
(defrule rule584
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule584 was fired" )
)
(defrule rule585
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule585 was fired" )
)
(defrule rule586
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule586 was fired" )
)
(defrule rule587
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule587 was fired" )
)
(defrule rule588
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule588 was fired" )
)
(defrule rule589
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule589 was fired" )
)
(defrule rule590
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule590 was fired" )
)
(defrule rule591
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule591 was fired" )
)
(defrule rule592
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule592 was fired" )
)
(defrule rule593
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule593 was fired" )
)
(defrule rule594
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule594 was fired" )
)
(defrule rule595
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule595 was fired" )
)
(defrule rule596
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule596 was fired" )
)
(defrule rule597
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule597 was fired" )
)
(defrule rule598
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule598 was fired" )
)
(defrule rule599
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule599 was fired" )
)
(defrule rule600
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule600 was fired" )
)
(defrule rule601
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule601 was fired" )
)
(defrule rule602
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule602 was fired" )
)
(defrule rule603
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule603 was fired" )
)
(defrule rule604
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule604 was fired" )
)
(defrule rule605
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule605 was fired" )
)
(defrule rule606
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule606 was fired" )
)
(defrule rule607
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule607 was fired" )
)
(defrule rule608
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule608 was fired" )
)
(defrule rule609
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule609 was fired" )
)
(defrule rule610
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule610 was fired" )
)
(defrule rule611
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule611 was fired" )
)
(defrule rule612
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule612 was fired" )
)
(defrule rule613
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule613 was fired" )
)
(defrule rule614
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule614 was fired" )
)
(defrule rule615
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule615 was fired" )
)
(defrule rule616
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule616 was fired" )
)
(defrule rule617
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule617 was fired" )
)
(defrule rule618
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule618 was fired" )
)
(defrule rule619
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule619 was fired" )
)
(defrule rule620
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule620 was fired" )
)
(defrule rule621
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule621 was fired" )
)
(defrule rule622
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule622 was fired" )
)
(defrule rule623
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule623 was fired" )
)
(defrule rule624
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule624 was fired" )
)
(defrule rule625
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule625 was fired" )
)
(defrule rule626
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule626 was fired" )
)
(defrule rule627
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule627 was fired" )
)
(defrule rule628
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule628 was fired" )
)
(defrule rule629
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule629 was fired" )
)
(defrule rule630
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule630 was fired" )
)
(defrule rule631
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule631 was fired" )
)
(defrule rule632
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule632 was fired" )
)
(defrule rule633
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule633 was fired" )
)
(defrule rule634
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule634 was fired" )
)
(defrule rule635
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule635 was fired" )
)
(defrule rule636
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule636 was fired" )
)
(defrule rule637
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule637 was fired" )
)
(defrule rule638
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule638 was fired" )
)
(defrule rule639
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule639 was fired" )
)
(defrule rule640
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule640 was fired" )
)
(defrule rule641
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule641 was fired" )
)
(defrule rule642
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule642 was fired" )
)
(defrule rule643
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule643 was fired" )
)
(defrule rule644
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule644 was fired" )
)
(defrule rule645
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule645 was fired" )
)
(defrule rule646
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule646 was fired" )
)
(defrule rule647
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule647 was fired" )
)
(defrule rule648
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule648 was fired" )
)
(defrule rule649
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule649 was fired" )
)
(defrule rule650
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule650 was fired" )
)
(defrule rule651
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule651 was fired" )
)
(defrule rule652
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule652 was fired" )
)
(defrule rule653
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule653 was fired" )
)
(defrule rule654
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule654 was fired" )
)
(defrule rule655
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule655 was fired" )
)
(defrule rule656
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule656 was fired" )
)
(defrule rule657
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule657 was fired" )
)
(defrule rule658
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule658 was fired" )
)
(defrule rule659
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule659 was fired" )
)
(defrule rule660
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule660 was fired" )
)
(defrule rule661
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule661 was fired" )
)
(defrule rule662
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule662 was fired" )
)
(defrule rule663
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule663 was fired" )
)
(defrule rule664
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule664 was fired" )
)
(defrule rule665
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule665 was fired" )
)
(defrule rule666
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule666 was fired" )
)
(defrule rule667
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule667 was fired" )
)
(defrule rule668
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule668 was fired" )
)
(defrule rule669
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule669 was fired" )
)
(defrule rule670
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule670 was fired" )
)
(defrule rule671
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule671 was fired" )
)
(defrule rule672
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule672 was fired" )
)
(defrule rule673
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule673 was fired" )
)
(defrule rule674
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule674 was fired" )
)
(defrule rule675
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule675 was fired" )
)
(defrule rule676
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule676 was fired" )
)
(defrule rule677
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule677 was fired" )
)
(defrule rule678
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule678 was fired" )
)
(defrule rule679
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule679 was fired" )
)
(defrule rule680
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule680 was fired" )
)
(defrule rule681
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule681 was fired" )
)
(defrule rule682
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule682 was fired" )
)
(defrule rule683
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule683 was fired" )
)
(defrule rule684
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule684 was fired" )
)
(defrule rule685
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule685 was fired" )
)
(defrule rule686
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule686 was fired" )
)
(defrule rule687
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule687 was fired" )
)
(defrule rule688
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule688 was fired" )
)
(defrule rule689
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule689 was fired" )
)
(defrule rule690
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule690 was fired" )
)
(defrule rule691
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule691 was fired" )
)
(defrule rule692
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule692 was fired" )
)
(defrule rule693
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule693 was fired" )
)
(defrule rule694
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule694 was fired" )
)
(defrule rule695
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule695 was fired" )
)
(defrule rule696
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule696 was fired" )
)
(defrule rule697
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule697 was fired" )
)
(defrule rule698
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule698 was fired" )
)
(defrule rule699
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule699 was fired" )
)
(defrule rule700
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule700 was fired" )
)
(defrule rule701
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule701 was fired" )
)
(defrule rule702
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule702 was fired" )
)
(defrule rule703
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule703 was fired" )
)
(defrule rule704
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule704 was fired" )
)
(defrule rule705
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule705 was fired" )
)
(defrule rule706
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule706 was fired" )
)
(defrule rule707
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule707 was fired" )
)
(defrule rule708
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule708 was fired" )
)
(defrule rule709
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule709 was fired" )
)
(defrule rule710
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule710 was fired" )
)
(defrule rule711
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule711 was fired" )
)
(defrule rule712
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule712 was fired" )
)
(defrule rule713
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule713 was fired" )
)
(defrule rule714
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule714 was fired" )
)
(defrule rule715
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule715 was fired" )
)
(defrule rule716
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule716 was fired" )
)
(defrule rule717
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule717 was fired" )
)
(defrule rule718
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule718 was fired" )
)
(defrule rule719
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule719 was fired" )
)
(defrule rule720
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule720 was fired" )
)
(defrule rule721
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule721 was fired" )
)
(defrule rule722
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule722 was fired" )
)
(defrule rule723
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule723 was fired" )
)
(defrule rule724
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule724 was fired" )
)
(defrule rule725
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule725 was fired" )
)
(defrule rule726
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule726 was fired" )
)
(defrule rule727
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule727 was fired" )
)
(defrule rule728
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule728 was fired" )
)
(defrule rule729
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule729 was fired" )
)
(defrule rule730
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule730 was fired" )
)
(defrule rule731
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule731 was fired" )
)
(defrule rule732
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule732 was fired" )
)
(defrule rule733
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule733 was fired" )
)
(defrule rule734
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule734 was fired" )
)
(defrule rule735
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule735 was fired" )
)
(defrule rule736
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule736 was fired" )
)
(defrule rule737
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule737 was fired" )
)
(defrule rule738
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule738 was fired" )
)
(defrule rule739
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule739 was fired" )
)
(defrule rule740
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule740 was fired" )
)
(defrule rule741
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule741 was fired" )
)
(defrule rule742
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule742 was fired" )
)
(defrule rule743
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule743 was fired" )
)
(defrule rule744
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule744 was fired" )
)
(defrule rule745
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule745 was fired" )
)
(defrule rule746
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule746 was fired" )
)
(defrule rule747
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule747 was fired" )
)
(defrule rule748
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule748 was fired" )
)
(defrule rule749
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule749 was fired" )
)
(defrule rule750
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule750 was fired" )
)
(defrule rule751
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule751 was fired" )
)
(defrule rule752
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule752 was fired" )
)
(defrule rule753
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule753 was fired" )
)
(defrule rule754
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule754 was fired" )
)
(defrule rule755
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule755 was fired" )
)
(defrule rule756
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule756 was fired" )
)
(defrule rule757
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule757 was fired" )
)
(defrule rule758
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule758 was fired" )
)
(defrule rule759
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule759 was fired" )
)
(defrule rule760
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule760 was fired" )
)
(defrule rule761
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule761 was fired" )
)
(defrule rule762
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule762 was fired" )
)
(defrule rule763
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule763 was fired" )
)
(defrule rule764
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule764 was fired" )
)
(defrule rule765
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule765 was fired" )
)
(defrule rule766
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule766 was fired" )
)
(defrule rule767
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule767 was fired" )
)
(defrule rule768
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule768 was fired" )
)
(defrule rule769
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule769 was fired" )
)
(defrule rule770
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule770 was fired" )
)
(defrule rule771
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule771 was fired" )
)
(defrule rule772
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule772 was fired" )
)
(defrule rule773
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule773 was fired" )
)
(defrule rule774
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule774 was fired" )
)
(defrule rule775
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule775 was fired" )
)
(defrule rule776
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule776 was fired" )
)
(defrule rule777
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule777 was fired" )
)
(defrule rule778
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule778 was fired" )
)
(defrule rule779
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule779 was fired" )
)
(defrule rule780
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule780 was fired" )
)
(defrule rule781
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule781 was fired" )
)
(defrule rule782
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule782 was fired" )
)
(defrule rule783
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule783 was fired" )
)
(defrule rule784
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule784 was fired" )
)
(defrule rule785
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule785 was fired" )
)
(defrule rule786
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule786 was fired" )
)
(defrule rule787
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule787 was fired" )
)
(defrule rule788
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule788 was fired" )
)
(defrule rule789
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule789 was fired" )
)
(defrule rule790
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule790 was fired" )
)
(defrule rule791
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule791 was fired" )
)
(defrule rule792
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule792 was fired" )
)
(defrule rule793
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule793 was fired" )
)
(defrule rule794
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule794 was fired" )
)
(defrule rule795
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule795 was fired" )
)
(defrule rule796
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule796 was fired" )
)
(defrule rule797
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule797 was fired" )
)
(defrule rule798
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule798 was fired" )
)
(defrule rule799
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule799 was fired" )
)
(defrule rule800
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule800 was fired" )
)
(defrule rule801
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule801 was fired" )
)
(defrule rule802
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule802 was fired" )
)
(defrule rule803
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule803 was fired" )
)
(defrule rule804
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule804 was fired" )
)
(defrule rule805
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule805 was fired" )
)
(defrule rule806
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule806 was fired" )
)
(defrule rule807
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule807 was fired" )
)
(defrule rule808
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule808 was fired" )
)
(defrule rule809
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule809 was fired" )
)
(defrule rule810
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule810 was fired" )
)
(defrule rule811
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule811 was fired" )
)
(defrule rule812
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule812 was fired" )
)
(defrule rule813
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule813 was fired" )
)
(defrule rule814
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule814 was fired" )
)
(defrule rule815
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule815 was fired" )
)
(defrule rule816
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule816 was fired" )
)
(defrule rule817
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule817 was fired" )
)
(defrule rule818
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule818 was fired" )
)
(defrule rule819
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule819 was fired" )
)
(defrule rule820
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule820 was fired" )
)
(defrule rule821
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule821 was fired" )
)
(defrule rule822
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule822 was fired" )
)
(defrule rule823
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule823 was fired" )
)
(defrule rule824
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule824 was fired" )
)
(defrule rule825
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule825 was fired" )
)
(defrule rule826
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule826 was fired" )
)
(defrule rule827
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule827 was fired" )
)
(defrule rule828
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule828 was fired" )
)
(defrule rule829
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule829 was fired" )
)
(defrule rule830
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule830 was fired" )
)
(defrule rule831
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule831 was fired" )
)
(defrule rule832
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule832 was fired" )
)
(defrule rule833
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule833 was fired" )
)
(defrule rule834
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule834 was fired" )
)
(defrule rule835
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule835 was fired" )
)
(defrule rule836
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule836 was fired" )
)
(defrule rule837
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule837 was fired" )
)
(defrule rule838
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule838 was fired" )
)
(defrule rule839
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule839 was fired" )
)
(defrule rule840
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule840 was fired" )
)
(defrule rule841
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule841 was fired" )
)
(defrule rule842
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule842 was fired" )
)
(defrule rule843
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule843 was fired" )
)
(defrule rule844
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule844 was fired" )
)
(defrule rule845
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule845 was fired" )
)
(defrule rule846
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule846 was fired" )
)
(defrule rule847
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule847 was fired" )
)
(defrule rule848
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule848 was fired" )
)
(defrule rule849
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule849 was fired" )
)
(defrule rule850
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule850 was fired" )
)
(defrule rule851
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule851 was fired" )
)
(defrule rule852
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule852 was fired" )
)
(defrule rule853
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule853 was fired" )
)
(defrule rule854
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule854 was fired" )
)
(defrule rule855
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule855 was fired" )
)
(defrule rule856
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule856 was fired" )
)
(defrule rule857
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule857 was fired" )
)
(defrule rule858
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule858 was fired" )
)
(defrule rule859
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule859 was fired" )
)
(defrule rule860
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule860 was fired" )
)
(defrule rule861
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule861 was fired" )
)
(defrule rule862
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule862 was fired" )
)
(defrule rule863
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule863 was fired" )
)
(defrule rule864
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule864 was fired" )
)
(defrule rule865
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule865 was fired" )
)
(defrule rule866
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule866 was fired" )
)
(defrule rule867
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule867 was fired" )
)
(defrule rule868
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule868 was fired" )
)
(defrule rule869
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule869 was fired" )
)
(defrule rule870
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule870 was fired" )
)
(defrule rule871
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule871 was fired" )
)
(defrule rule872
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule872 was fired" )
)
(defrule rule873
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule873 was fired" )
)
(defrule rule874
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule874 was fired" )
)
(defrule rule875
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule875 was fired" )
)
(defrule rule876
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule876 was fired" )
)
(defrule rule877
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule877 was fired" )
)
(defrule rule878
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule878 was fired" )
)
(defrule rule879
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule879 was fired" )
)
(defrule rule880
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule880 was fired" )
)
(defrule rule881
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule881 was fired" )
)
(defrule rule882
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule882 was fired" )
)
(defrule rule883
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule883 was fired" )
)
(defrule rule884
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule884 was fired" )
)
(defrule rule885
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule885 was fired" )
)
(defrule rule886
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule886 was fired" )
)
(defrule rule887
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule887 was fired" )
)
(defrule rule888
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule888 was fired" )
)
(defrule rule889
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule889 was fired" )
)
(defrule rule890
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule890 was fired" )
)
(defrule rule891
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251040)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule891 was fired" )
)
(defrule rule892
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule892 was fired" )
)
(defrule rule893
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule893 was fired" )
)
(defrule rule894
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule894 was fired" )
)
(defrule rule895
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule895 was fired" )
)
(defrule rule896
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule896 was fired" )
)
(defrule rule897
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule897 was fired" )
)
(defrule rule898
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule898 was fired" )
)
(defrule rule899
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule899 was fired" )
)
(defrule rule900
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule900 was fired" )
)
(defrule rule901
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule901 was fired" )
)
(defrule rule902
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule902 was fired" )
)
(defrule rule903
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule903 was fired" )
)
(defrule rule904
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule904 was fired" )
)
(defrule rule905
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule905 was fired" )
)
(defrule rule906
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule906 was fired" )
)
(defrule rule907
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule907 was fired" )
)
(defrule rule908
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule908 was fired" )
)
(defrule rule909
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule909 was fired" )
)
(defrule rule910
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule910 was fired" )
)
(defrule rule911
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule911 was fired" )
)
(defrule rule912
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule912 was fired" )
)
(defrule rule913
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule913 was fired" )
)
(defrule rule914
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule914 was fired" )
)
(defrule rule915
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule915 was fired" )
)
(defrule rule916
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule916 was fired" )
)
(defrule rule917
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule917 was fired" )
)
(defrule rule918
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule918 was fired" )
)
(defrule rule919
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule919 was fired" )
)
(defrule rule920
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule920 was fired" )
)
(defrule rule921
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule921 was fired" )
)
(defrule rule922
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule922 was fired" )
)
(defrule rule923
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule923 was fired" )
)
(defrule rule924
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule924 was fired" )
)
(defrule rule925
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule925 was fired" )
)
(defrule rule926
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule926 was fired" )
)
(defrule rule927
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule927 was fired" )
)
(defrule rule928
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule928 was fired" )
)
(defrule rule929
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule929 was fired" )
)
(defrule rule930
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule930 was fired" )
)
(defrule rule931
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule931 was fired" )
)
(defrule rule932
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule932 was fired" )
)
(defrule rule933
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule933 was fired" )
)
(defrule rule934
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule934 was fired" )
)
(defrule rule935
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule935 was fired" )
)
(defrule rule936
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule936 was fired" )
)
(defrule rule937
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule937 was fired" )
)
(defrule rule938
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule938 was fired" )
)
(defrule rule939
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule939 was fired" )
)
(defrule rule940
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule940 was fired" )
)
(defrule rule941
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule941 was fired" )
)
(defrule rule942
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule942 was fired" )
)
(defrule rule943
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule943 was fired" )
)
(defrule rule944
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule944 was fired" )
)
(defrule rule945
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule945 was fired" )
)
(defrule rule946
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule946 was fired" )
)
(defrule rule947
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule947 was fired" )
)
(defrule rule948
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule948 was fired" )
)
(defrule rule949
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule949 was fired" )
)
(defrule rule950
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule950 was fired" )
)
(defrule rule951
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule951 was fired" )
)
(defrule rule952
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule952 was fired" )
)
(defrule rule953
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule953 was fired" )
)
(defrule rule954
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule954 was fired" )
)
(defrule rule955
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule955 was fired" )
)
(defrule rule956
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule956 was fired" )
)
(defrule rule957
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule957 was fired" )
)
(defrule rule958
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule958 was fired" )
)
(defrule rule959
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule959 was fired" )
)
(defrule rule960
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule960 was fired" )
)
(defrule rule961
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule961 was fired" )
)
(defrule rule962
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule962 was fired" )
)
(defrule rule963
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule963 was fired" )
)
(defrule rule964
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule964 was fired" )
)
(defrule rule965
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule965 was fired" )
)
(defrule rule966
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule966 was fired" )
)
(defrule rule967
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule967 was fired" )
)
(defrule rule968
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule968 was fired" )
)
(defrule rule969
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule969 was fired" )
)
(defrule rule970
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule970 was fired" )
)
(defrule rule971
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule971 was fired" )
)
(defrule rule972
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule972 was fired" )
)
(defrule rule973
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule973 was fired" )
)
(defrule rule974
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule974 was fired" )
)
(defrule rule975
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule975 was fired" )
)
(defrule rule976
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule976 was fired" )
)
(defrule rule977
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule977 was fired" )
)
(defrule rule978
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule978 was fired" )
)
(defrule rule979
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule979 was fired" )
)
(defrule rule980
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule980 was fired" )
)
(defrule rule981
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule981 was fired" )
)
(defrule rule982
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule982 was fired" )
)
(defrule rule983
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule983 was fired" )
)
(defrule rule984
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule984 was fired" )
)
(defrule rule985
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule985 was fired" )
)
(defrule rule986
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule986 was fired" )
)
(defrule rule987
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule987 was fired" )
)
(defrule rule988
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule988 was fired" )
)
(defrule rule989
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule989 was fired" )
)
(defrule rule990
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule990 was fired" )
)
(defrule rule991
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule991 was fired" )
)
(defrule rule992
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule992 was fired" )
)
(defrule rule993
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule993 was fired" )
)
(defrule rule994
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule994 was fired" )
)
(defrule rule995
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule995 was fired" )
)
(defrule rule996
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule996 was fired" )
)
(defrule rule997
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule997 was fired" )
)
(defrule rule998
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule998 was fired" )
)
(defrule rule999
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule999 was fired" )
)
(defrule rule1000
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1000 was fired" )
)
(defrule rule1001
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1001 was fired" )
)
(defrule rule1002
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1002 was fired" )
)
(defrule rule1003
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1003 was fired" )
)
(defrule rule1004
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1004 was fired" )
)
(defrule rule1005
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1005 was fired" )
)
(defrule rule1006
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1006 was fired" )
)
(defrule rule1007
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1007 was fired" )
)
(defrule rule1008
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1008 was fired" )
)
(defrule rule1009
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1009 was fired" )
)
(defrule rule1010
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1010 was fired" )
)
(defrule rule1011
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1011 was fired" )
)
(defrule rule1012
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1012 was fired" )
)
(defrule rule1013
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1013 was fired" )
)
(defrule rule1014
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1014 was fired" )
)
(defrule rule1015
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1015 was fired" )
)
(defrule rule1016
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1016 was fired" )
)
(defrule rule1017
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1017 was fired" )
)
(defrule rule1018
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1018 was fired" )
)
(defrule rule1019
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1019 was fired" )
)
(defrule rule1020
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1020 was fired" )
)
(defrule rule1021
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1021 was fired" )
)
(defrule rule1022
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1022 was fired" )
)
(defrule rule1023
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1023 was fired" )
)
(defrule rule1024
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1024 was fired" )
)
(defrule rule1025
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1025 was fired" )
)
(defrule rule1026
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1026 was fired" )
)
(defrule rule1027
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1027 was fired" )
)
(defrule rule1028
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1028 was fired" )
)
(defrule rule1029
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1029 was fired" )
)
(defrule rule1030
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1030 was fired" )
)
(defrule rule1031
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1031 was fired" )
)
(defrule rule1032
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1032 was fired" )
)
(defrule rule1033
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1033 was fired" )
)
(defrule rule1034
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1034 was fired" )
)
(defrule rule1035
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1035 was fired" )
)
(defrule rule1036
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1036 was fired" )
)
(defrule rule1037
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1037 was fired" )
)
(defrule rule1038
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1038 was fired" )
)
(defrule rule1039
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1039 was fired" )
)
(defrule rule1040
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1040 was fired" )
)
(defrule rule1041
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1041 was fired" )
)
(defrule rule1042
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1042 was fired" )
)
(defrule rule1043
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1043 was fired" )
)
(defrule rule1044
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1044 was fired" )
)
(defrule rule1045
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1045 was fired" )
)
(defrule rule1046
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1046 was fired" )
)
(defrule rule1047
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1047 was fired" )
)
(defrule rule1048
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1048 was fired" )
)
(defrule rule1049
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1049 was fired" )
)
(defrule rule1050
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1050 was fired" )
)
(defrule rule1051
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1051 was fired" )
)
(defrule rule1052
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1052 was fired" )
)
(defrule rule1053
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1053 was fired" )
)
(defrule rule1054
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1054 was fired" )
)
(defrule rule1055
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1055 was fired" )
)
(defrule rule1056
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1056 was fired" )
)
(defrule rule1057
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1057 was fired" )
)
(defrule rule1058
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1058 was fired" )
)
(defrule rule1059
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1059 was fired" )
)
(defrule rule1060
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1060 was fired" )
)
(defrule rule1061
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1061 was fired" )
)
(defrule rule1062
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1062 was fired" )
)
(defrule rule1063
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1063 was fired" )
)
(defrule rule1064
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1064 was fired" )
)
(defrule rule1065
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1065 was fired" )
)
(defrule rule1066
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1066 was fired" )
)
(defrule rule1067
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1067 was fired" )
)
(defrule rule1068
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1068 was fired" )
)
(defrule rule1069
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1069 was fired" )
)
(defrule rule1070
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1070 was fired" )
)
(defrule rule1071
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1071 was fired" )
)
(defrule rule1072
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1072 was fired" )
)
(defrule rule1073
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1073 was fired" )
)
(defrule rule1074
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1074 was fired" )
)
(defrule rule1075
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1075 was fired" )
)
(defrule rule1076
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1076 was fired" )
)
(defrule rule1077
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1077 was fired" )
)
(defrule rule1078
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1078 was fired" )
)
(defrule rule1079
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1079 was fired" )
)
(defrule rule1080
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1080 was fired" )
)
(defrule rule1081
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1081 was fired" )
)
(defrule rule1082
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1082 was fired" )
)
(defrule rule1083
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1083 was fired" )
)
(defrule rule1084
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1084 was fired" )
)
(defrule rule1085
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1085 was fired" )
)
(defrule rule1086
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1086 was fired" )
)
(defrule rule1087
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1087 was fired" )
)
(defrule rule1088
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1088 was fired" )
)
(defrule rule1089
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1089 was fired" )
)
(defrule rule1090
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1090 was fired" )
)
(defrule rule1091
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1091 was fired" )
)
(defrule rule1092
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1092 was fired" )
)
(defrule rule1093
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1093 was fired" )
)
(defrule rule1094
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1094 was fired" )
)
(defrule rule1095
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1095 was fired" )
)
(defrule rule1096
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1096 was fired" )
)
(defrule rule1097
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1097 was fired" )
)
(defrule rule1098
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1098 was fired" )
)
(defrule rule1099
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1099 was fired" )
)
(defrule rule1100
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1100 was fired" )
)
(defrule rule1101
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1101 was fired" )
)
(defrule rule1102
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1102 was fired" )
)
(defrule rule1103
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1103 was fired" )
)
(defrule rule1104
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1104 was fired" )
)
(defrule rule1105
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1105 was fired" )
)
(defrule rule1106
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1106 was fired" )
)
(defrule rule1107
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1107 was fired" )
)
(defrule rule1108
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1108 was fired" )
)
(defrule rule1109
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1109 was fired" )
)
(defrule rule1110
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1110 was fired" )
)
(defrule rule1111
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1111 was fired" )
)
(defrule rule1112
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1112 was fired" )
)
(defrule rule1113
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1113 was fired" )
)
(defrule rule1114
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1114 was fired" )
)
(defrule rule1115
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1115 was fired" )
)
(defrule rule1116
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1116 was fired" )
)
(defrule rule1117
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1117 was fired" )
)
(defrule rule1118
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1118 was fired" )
)
(defrule rule1119
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1119 was fired" )
)
(defrule rule1120
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1120 was fired" )
)
(defrule rule1121
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1121 was fired" )
)
(defrule rule1122
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1122 was fired" )
)
(defrule rule1123
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1123 was fired" )
)
(defrule rule1124
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1124 was fired" )
)
(defrule rule1125
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1125 was fired" )
)
(defrule rule1126
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1126 was fired" )
)
(defrule rule1127
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1127 was fired" )
)
(defrule rule1128
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1128 was fired" )
)
(defrule rule1129
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1129 was fired" )
)
(defrule rule1130
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1130 was fired" )
)
(defrule rule1131
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1131 was fired" )
)
(defrule rule1132
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1132 was fired" )
)
(defrule rule1133
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1133 was fired" )
)
(defrule rule1134
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1134 was fired" )
)
(defrule rule1135
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1135 was fired" )
)
(defrule rule1136
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1136 was fired" )
)
(defrule rule1137
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1137 was fired" )
)
(defrule rule1138
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1138 was fired" )
)
(defrule rule1139
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1139 was fired" )
)
(defrule rule1140
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1140 was fired" )
)
(defrule rule1141
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1141 was fired" )
)
(defrule rule1142
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1142 was fired" )
)
(defrule rule1143
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1143 was fired" )
)
(defrule rule1144
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1144 was fired" )
)
(defrule rule1145
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1145 was fired" )
)
(defrule rule1146
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1146 was fired" )
)
(defrule rule1147
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1147 was fired" )
)
(defrule rule1148
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1148 was fired" )
)
(defrule rule1149
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1149 was fired" )
)
(defrule rule1150
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1150 was fired" )
)
(defrule rule1151
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1151 was fired" )
)
(defrule rule1152
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1152 was fired" )
)
(defrule rule1153
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1153 was fired" )
)
(defrule rule1154
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1154 was fired" )
)
(defrule rule1155
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1155 was fired" )
)
(defrule rule1156
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1156 was fired" )
)
(defrule rule1157
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1157 was fired" )
)
(defrule rule1158
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1158 was fired" )
)
(defrule rule1159
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1159 was fired" )
)
(defrule rule1160
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1160 was fired" )
)
(defrule rule1161
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1161 was fired" )
)
(defrule rule1162
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1162 was fired" )
)
(defrule rule1163
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1163 was fired" )
)
(defrule rule1164
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1164 was fired" )
)
(defrule rule1165
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1165 was fired" )
)
(defrule rule1166
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1166 was fired" )
)
(defrule rule1167
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1167 was fired" )
)
(defrule rule1168
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1168 was fired" )
)
(defrule rule1169
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1169 was fired" )
)
(defrule rule1170
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1170 was fired" )
)
(defrule rule1171
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1171 was fired" )
)
(defrule rule1172
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1172 was fired" )
)
(defrule rule1173
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1173 was fired" )
)
(defrule rule1174
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1174 was fired" )
)
(defrule rule1175
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1175 was fired" )
)
(defrule rule1176
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1176 was fired" )
)
(defrule rule1177
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1177 was fired" )
)
(defrule rule1178
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1178 was fired" )
)
(defrule rule1179
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1179 was fired" )
)
(defrule rule1180
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1180 was fired" )
)
(defrule rule1181
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1181 was fired" )
)
(defrule rule1182
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1182 was fired" )
)
(defrule rule1183
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1183 was fired" )
)
(defrule rule1184
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1184 was fired" )
)
(defrule rule1185
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1185 was fired" )
)
(defrule rule1186
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1186 was fired" )
)
(defrule rule1187
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1187 was fired" )
)
(defrule rule1188
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1188 was fired" )
)
(defrule rule1189
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1189 was fired" )
)
(defrule rule1190
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1190 was fired" )
)
(defrule rule1191
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1191 was fired" )
)
(defrule rule1192
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1192 was fired" )
)
(defrule rule1193
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1193 was fired" )
)
(defrule rule1194
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1194 was fired" )
)
(defrule rule1195
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1195 was fired" )
)
(defrule rule1196
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1196 was fired" )
)
(defrule rule1197
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1197 was fired" )
)
(defrule rule1198
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1198 was fired" )
)
(defrule rule1199
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1199 was fired" )
)
(defrule rule1200
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1200 was fired" )
)
(defrule rule1201
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1201 was fired" )
)
(defrule rule1202
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1202 was fired" )
)
(defrule rule1203
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1203 was fired" )
)
(defrule rule1204
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1204 was fired" )
)
(defrule rule1205
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1205 was fired" )
)
(defrule rule1206
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1206 was fired" )
)
(defrule rule1207
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1207 was fired" )
)
(defrule rule1208
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1208 was fired" )
)
(defrule rule1209
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1209 was fired" )
)
(defrule rule1210
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1210 was fired" )
)
(defrule rule1211
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1211 was fired" )
)
(defrule rule1212
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1212 was fired" )
)
(defrule rule1213
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1213 was fired" )
)
(defrule rule1214
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1214 was fired" )
)
(defrule rule1215
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1215 was fired" )
)
(defrule rule1216
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1216 was fired" )
)
(defrule rule1217
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1217 was fired" )
)
(defrule rule1218
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1218 was fired" )
)
(defrule rule1219
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1219 was fired" )
)
(defrule rule1220
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1220 was fired" )
)
(defrule rule1221
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1221 was fired" )
)
(defrule rule1222
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1222 was fired" )
)
(defrule rule1223
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1223 was fired" )
)
(defrule rule1224
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1224 was fired" )
)
(defrule rule1225
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1225 was fired" )
)
(defrule rule1226
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1226 was fired" )
)
(defrule rule1227
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1227 was fired" )
)
(defrule rule1228
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1228 was fired" )
)
(defrule rule1229
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1229 was fired" )
)
(defrule rule1230
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1230 was fired" )
)
(defrule rule1231
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1231 was fired" )
)
(defrule rule1232
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1232 was fired" )
)
(defrule rule1233
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1233 was fired" )
)
(defrule rule1234
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1234 was fired" )
)
(defrule rule1235
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1235 was fired" )
)
(defrule rule1236
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1236 was fired" )
)
(defrule rule1237
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1237 was fired" )
)
(defrule rule1238
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1238 was fired" )
)
(defrule rule1239
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1239 was fired" )
)
(defrule rule1240
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1240 was fired" )
)
(defrule rule1241
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1241 was fired" )
)
(defrule rule1242
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1242 was fired" )
)
(defrule rule1243
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1243 was fired" )
)
(defrule rule1244
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1244 was fired" )
)
(defrule rule1245
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1245 was fired" )
)
(defrule rule1246
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1246 was fired" )
)
(defrule rule1247
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1247 was fired" )
)
(defrule rule1248
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1248 was fired" )
)
(defrule rule1249
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1249 was fired" )
)
(defrule rule1250
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1250 was fired" )
)
(defrule rule1251
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1251 was fired" )
)
(defrule rule1252
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1252 was fired" )
)
(defrule rule1253
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1253 was fired" )
)
(defrule rule1254
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1254 was fired" )
)
(defrule rule1255
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1255 was fired" )
)
(defrule rule1256
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1256 was fired" )
)
(defrule rule1257
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1257 was fired" )
)
(defrule rule1258
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1258 was fired" )
)
(defrule rule1259
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1259 was fired" )
)
(defrule rule1260
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1260 was fired" )
)
(defrule rule1261
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1261 was fired" )
)
(defrule rule1262
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1262 was fired" )
)
(defrule rule1263
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1263 was fired" )
)
(defrule rule1264
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1264 was fired" )
)
(defrule rule1265
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1265 was fired" )
)
(defrule rule1266
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1266 was fired" )
)
(defrule rule1267
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1267 was fired" )
)
(defrule rule1268
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1268 was fired" )
)
(defrule rule1269
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1269 was fired" )
)
(defrule rule1270
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1270 was fired" )
)
(defrule rule1271
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1271 was fired" )
)
(defrule rule1272
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1272 was fired" )
)
(defrule rule1273
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1273 was fired" )
)
(defrule rule1274
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1274 was fired" )
)
(defrule rule1275
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1275 was fired" )
)
(defrule rule1276
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1276 was fired" )
)
(defrule rule1277
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1277 was fired" )
)
(defrule rule1278
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1278 was fired" )
)
(defrule rule1279
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1279 was fired" )
)
(defrule rule1280
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1280 was fired" )
)
(defrule rule1281
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1281 was fired" )
)
(defrule rule1282
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1282 was fired" )
)
(defrule rule1283
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1283 was fired" )
)
(defrule rule1284
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1284 was fired" )
)
(defrule rule1285
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1285 was fired" )
)
(defrule rule1286
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1286 was fired" )
)
(defrule rule1287
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1287 was fired" )
)
(defrule rule1288
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1288 was fired" )
)
(defrule rule1289
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1289 was fired" )
)
(defrule rule1290
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1290 was fired" )
)
(defrule rule1291
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1291 was fired" )
)
(defrule rule1292
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1292 was fired" )
)
(defrule rule1293
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1293 was fired" )
)
(defrule rule1294
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1294 was fired" )
)
(defrule rule1295
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1295 was fired" )
)
(defrule rule1296
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1296 was fired" )
)
(defrule rule1297
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1297 was fired" )
)
(defrule rule1298
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1298 was fired" )
)
(defrule rule1299
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1299 was fired" )
)
(defrule rule1300
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1300 was fired" )
)
(defrule rule1301
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1301 was fired" )
)
(defrule rule1302
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1302 was fired" )
)
(defrule rule1303
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1303 was fired" )
)
(defrule rule1304
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1304 was fired" )
)
(defrule rule1305
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1305 was fired" )
)
(defrule rule1306
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1306 was fired" )
)
(defrule rule1307
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1307 was fired" )
)
(defrule rule1308
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1308 was fired" )
)
(defrule rule1309
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1309 was fired" )
)
(defrule rule1310
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1310 was fired" )
)
(defrule rule1311
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1311 was fired" )
)
(defrule rule1312
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1312 was fired" )
)
(defrule rule1313
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1313 was fired" )
)
(defrule rule1314
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1314 was fired" )
)
(defrule rule1315
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1315 was fired" )
)
(defrule rule1316
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1316 was fired" )
)
(defrule rule1317
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1317 was fired" )
)
(defrule rule1318
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1318 was fired" )
)
(defrule rule1319
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1319 was fired" )
)
(defrule rule1320
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1320 was fired" )
)
(defrule rule1321
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1321 was fired" )
)
(defrule rule1322
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1322 was fired" )
)
(defrule rule1323
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1323 was fired" )
)
(defrule rule1324
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1324 was fired" )
)
(defrule rule1325
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1325 was fired" )
)
(defrule rule1326
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1326 was fired" )
)
(defrule rule1327
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1327 was fired" )
)
(defrule rule1328
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1328 was fired" )
)
(defrule rule1329
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1329 was fired" )
)
(defrule rule1330
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1330 was fired" )
)
(defrule rule1331
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1331 was fired" )
)
(defrule rule1332
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1332 was fired" )
)
(defrule rule1333
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1333 was fired" )
)
(defrule rule1334
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1334 was fired" )
)
(defrule rule1335
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1335 was fired" )
)
(defrule rule1336
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1336 was fired" )
)
(defrule rule1337
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1337 was fired" )
)
(defrule rule1338
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1338 was fired" )
)
(defrule rule1339
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1339 was fired" )
)
(defrule rule1340
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1340 was fired" )
)
(defrule rule1341
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1341 was fired" )
)
(defrule rule1342
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1342 was fired" )
)
(defrule rule1343
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1343 was fired" )
)
(defrule rule1344
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1344 was fired" )
)
(defrule rule1345
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1345 was fired" )
)
(defrule rule1346
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1346 was fired" )
)
(defrule rule1347
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1347 was fired" )
)
(defrule rule1348
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1348 was fired" )
)
(defrule rule1349
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1349 was fired" )
)
(defrule rule1350
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1350 was fired" )
)
(defrule rule1351
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1351 was fired" )
)
(defrule rule1352
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1352 was fired" )
)
(defrule rule1353
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1353 was fired" )
)
(defrule rule1354
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1354 was fired" )
)
(defrule rule1355
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1355 was fired" )
)
(defrule rule1356
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1356 was fired" )
)
(defrule rule1357
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1357 was fired" )
)
(defrule rule1358
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1358 was fired" )
)
(defrule rule1359
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1359 was fired" )
)
(defrule rule1360
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1360 was fired" )
)
(defrule rule1361
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1361 was fired" )
)
(defrule rule1362
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1362 was fired" )
)
(defrule rule1363
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1363 was fired" )
)
(defrule rule1364
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1364 was fired" )
)
(defrule rule1365
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1365 was fired" )
)
(defrule rule1366
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1366 was fired" )
)
(defrule rule1367
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1367 was fired" )
)
(defrule rule1368
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1368 was fired" )
)
(defrule rule1369
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1369 was fired" )
)
(defrule rule1370
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1370 was fired" )
)
(defrule rule1371
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1371 was fired" )
)
(defrule rule1372
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1372 was fired" )
)
(defrule rule1373
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1373 was fired" )
)
(defrule rule1374
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1374 was fired" )
)
(defrule rule1375
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1375 was fired" )
)
(defrule rule1376
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1376 was fired" )
)
(defrule rule1377
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1377 was fired" )
)
(defrule rule1378
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1378 was fired" )
)
(defrule rule1379
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1379 was fired" )
)
(defrule rule1380
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1380 was fired" )
)
(defrule rule1381
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1381 was fired" )
)
(defrule rule1382
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1382 was fired" )
)
(defrule rule1383
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1383 was fired" )
)
(defrule rule1384
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1384 was fired" )
)
(defrule rule1385
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1385 was fired" )
)
(defrule rule1386
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1386 was fired" )
)
(defrule rule1387
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1387 was fired" )
)
(defrule rule1388
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1388 was fired" )
)
(defrule rule1389
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1389 was fired" )
)
(defrule rule1390
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1390 was fired" )
)
(defrule rule1391
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1391 was fired" )
)
(defrule rule1392
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1392 was fired" )
)
(defrule rule1393
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1393 was fired" )
)
(defrule rule1394
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1394 was fired" )
)
(defrule rule1395
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1395 was fired" )
)
(defrule rule1396
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1396 was fired" )
)
(defrule rule1397
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1397 was fired" )
)
(defrule rule1398
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1398 was fired" )
)
(defrule rule1399
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1399 was fired" )
)
(defrule rule1400
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1400 was fired" )
)
(defrule rule1401
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1401 was fired" )
)
(defrule rule1402
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1402 was fired" )
)
(defrule rule1403
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1403 was fired" )
)
(defrule rule1404
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1404 was fired" )
)
(defrule rule1405
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1405 was fired" )
)
(defrule rule1406
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1406 was fired" )
)
(defrule rule1407
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1407 was fired" )
)
(defrule rule1408
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1408 was fired" )
)
(defrule rule1409
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1409 was fired" )
)
(defrule rule1410
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1410 was fired" )
)
(defrule rule1411
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1411 was fired" )
)
(defrule rule1412
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1412 was fired" )
)
(defrule rule1413
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1413 was fired" )
)
(defrule rule1414
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1414 was fired" )
)
(defrule rule1415
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1415 was fired" )
)
(defrule rule1416
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1416 was fired" )
)
(defrule rule1417
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1417 was fired" )
)
(defrule rule1418
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1418 was fired" )
)
(defrule rule1419
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1419 was fired" )
)
(defrule rule1420
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1420 was fired" )
)
(defrule rule1421
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1421 was fired" )
)
(defrule rule1422
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1422 was fired" )
)
(defrule rule1423
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1423 was fired" )
)
(defrule rule1424
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1424 was fired" )
)
(defrule rule1425
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1425 was fired" )
)
(defrule rule1426
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1426 was fired" )
)
(defrule rule1427
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1427 was fired" )
)
(defrule rule1428
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1428 was fired" )
)
(defrule rule1429
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1429 was fired" )
)
(defrule rule1430
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1430 was fired" )
)
(defrule rule1431
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1431 was fired" )
)
(defrule rule1432
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1432 was fired" )
)
(defrule rule1433
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1433 was fired" )
)
(defrule rule1434
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1434 was fired" )
)
(defrule rule1435
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1435 was fired" )
)
(defrule rule1436
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1436 was fired" )
)
(defrule rule1437
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1437 was fired" )
)
(defrule rule1438
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1438 was fired" )
)
(defrule rule1439
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1439 was fired" )
)
(defrule rule1440
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1440 was fired" )
)
(defrule rule1441
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1441 was fired" )
)
(defrule rule1442
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1442 was fired" )
)
(defrule rule1443
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1443 was fired" )
)
(defrule rule1444
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1444 was fired" )
)
(defrule rule1445
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1445 was fired" )
)
(defrule rule1446
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1446 was fired" )
)
(defrule rule1447
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1447 was fired" )
)
(defrule rule1448
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1448 was fired" )
)
(defrule rule1449
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1449 was fired" )
)
(defrule rule1450
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1450 was fired" )
)
(defrule rule1451
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1451 was fired" )
)
(defrule rule1452
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1452 was fired" )
)
(defrule rule1453
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1453 was fired" )
)
(defrule rule1454
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1454 was fired" )
)
(defrule rule1455
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1455 was fired" )
)
(defrule rule1456
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1456 was fired" )
)
(defrule rule1457
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1457 was fired" )
)
(defrule rule1458
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1458 was fired" )
)
(defrule rule1459
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1459 was fired" )
)
(defrule rule1460
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1460 was fired" )
)
(defrule rule1461
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1461 was fired" )
)
(defrule rule1462
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1462 was fired" )
)
(defrule rule1463
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1463 was fired" )
)
(defrule rule1464
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1464 was fired" )
)
(defrule rule1465
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1465 was fired" )
)
(defrule rule1466
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1466 was fired" )
)
(defrule rule1467
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1467 was fired" )
)
(defrule rule1468
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1468 was fired" )
)
(defrule rule1469
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1469 was fired" )
)
(defrule rule1470
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1470 was fired" )
)
(defrule rule1471
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1471 was fired" )
)
(defrule rule1472
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1472 was fired" )
)
(defrule rule1473
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1473 was fired" )
)
(defrule rule1474
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1474 was fired" )
)
(defrule rule1475
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1475 was fired" )
)
(defrule rule1476
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1476 was fired" )
)
(defrule rule1477
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1477 was fired" )
)
(defrule rule1478
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1478 was fired" )
)
(defrule rule1479
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1479 was fired" )
)
(defrule rule1480
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1480 was fired" )
)
(defrule rule1481
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1481 was fired" )
)
(defrule rule1482
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1482 was fired" )
)
(defrule rule1483
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1483 was fired" )
)
(defrule rule1484
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1484 was fired" )
)
(defrule rule1485
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1485 was fired" )
)
(defrule rule1486
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1486 was fired" )
)
(defrule rule1487
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1487 was fired" )
)
(defrule rule1488
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1488 was fired" )
)
(defrule rule1489
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1489 was fired" )
)
(defrule rule1490
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1490 was fired" )
)
(defrule rule1491
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1491 was fired" )
)
(defrule rule1492
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1492 was fired" )
)
(defrule rule1493
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1493 was fired" )
)
(defrule rule1494
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1494 was fired" )
)
(defrule rule1495
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1495 was fired" )
)
(defrule rule1496
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1496 was fired" )
)
(defrule rule1497
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1497 was fired" )
)
(defrule rule1498
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1498 was fired" )
)
(defrule rule1499
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1499 was fired" )
)
(defrule rule1500
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1500 was fired" )
)
(defrule rule1501
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1501 was fired" )
)
(defrule rule1502
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1502 was fired" )
)
(defrule rule1503
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1503 was fired" )
)
(defrule rule1504
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1504 was fired" )
)
(defrule rule1505
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1505 was fired" )
)
(defrule rule1506
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1506 was fired" )
)
(defrule rule1507
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1507 was fired" )
)
(defrule rule1508
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1508 was fired" )
)
(defrule rule1509
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1509 was fired" )
)
(defrule rule1510
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1510 was fired" )
)
(defrule rule1511
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1511 was fired" )
)
(defrule rule1512
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1512 was fired" )
)
(defrule rule1513
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1513 was fired" )
)
(defrule rule1514
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1514 was fired" )
)
(defrule rule1515
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1515 was fired" )
)
(defrule rule1516
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1516 was fired" )
)
(defrule rule1517
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1517 was fired" )
)
(defrule rule1518
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1518 was fired" )
)
(defrule rule1519
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1519 was fired" )
)
(defrule rule1520
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1520 was fired" )
)
(defrule rule1521
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1521 was fired" )
)
(defrule rule1522
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1522 was fired" )
)
(defrule rule1523
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1523 was fired" )
)
(defrule rule1524
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1524 was fired" )
)
(defrule rule1525
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1525 was fired" )
)
(defrule rule1526
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1526 was fired" )
)
(defrule rule1527
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1527 was fired" )
)
(defrule rule1528
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1528 was fired" )
)
(defrule rule1529
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1529 was fired" )
)
(defrule rule1530
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1530 was fired" )
)
(defrule rule1531
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1531 was fired" )
)
(defrule rule1532
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1532 was fired" )
)
(defrule rule1533
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1533 was fired" )
)
(defrule rule1534
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1534 was fired" )
)
(defrule rule1535
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1535 was fired" )
)
(defrule rule1536
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1536 was fired" )
)
(defrule rule1537
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1537 was fired" )
)
(defrule rule1538
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1538 was fired" )
)
(defrule rule1539
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1539 was fired" )
)
(defrule rule1540
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1540 was fired" )
)
(defrule rule1541
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1541 was fired" )
)
(defrule rule1542
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1542 was fired" )
)
(defrule rule1543
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1543 was fired" )
)
(defrule rule1544
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1544 was fired" )
)
(defrule rule1545
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1545 was fired" )
)
(defrule rule1546
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1546 was fired" )
)
(defrule rule1547
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1547 was fired" )
)
(defrule rule1548
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1548 was fired" )
)
(defrule rule1549
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1549 was fired" )
)
(defrule rule1550
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1550 was fired" )
)
(defrule rule1551
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1551 was fired" )
)
(defrule rule1552
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1552 was fired" )
)
(defrule rule1553
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1553 was fired" )
)
(defrule rule1554
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1554 was fired" )
)
(defrule rule1555
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1555 was fired" )
)
(defrule rule1556
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1556 was fired" )
)
(defrule rule1557
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1557 was fired" )
)
(defrule rule1558
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1558 was fired" )
)
(defrule rule1559
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1559 was fired" )
)
(defrule rule1560
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1560 was fired" )
)
(defrule rule1561
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1561 was fired" )
)
(defrule rule1562
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1562 was fired" )
)
(defrule rule1563
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1563 was fired" )
)
(defrule rule1564
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1564 was fired" )
)
(defrule rule1565
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1565 was fired" )
)
(defrule rule1566
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1566 was fired" )
)
(defrule rule1567
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1567 was fired" )
)
(defrule rule1568
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1568 was fired" )
)
(defrule rule1569
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1569 was fired" )
)
(defrule rule1570
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1570 was fired" )
)
(defrule rule1571
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1571 was fired" )
)
(defrule rule1572
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1572 was fired" )
)
(defrule rule1573
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1573 was fired" )
)
(defrule rule1574
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1574 was fired" )
)
(defrule rule1575
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1575 was fired" )
)
(defrule rule1576
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1576 was fired" )
)
(defrule rule1577
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1577 was fired" )
)
(defrule rule1578
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1578 was fired" )
)
(defrule rule1579
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1579 was fired" )
)
(defrule rule1580
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1580 was fired" )
)
(defrule rule1581
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1581 was fired" )
)
(defrule rule1582
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1582 was fired" )
)
(defrule rule1583
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1583 was fired" )
)
(defrule rule1584
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1584 was fired" )
)
(defrule rule1585
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1585 was fired" )
)
(defrule rule1586
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1586 was fired" )
)
(defrule rule1587
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1587 was fired" )
)
(defrule rule1588
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1588 was fired" )
)
(defrule rule1589
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1589 was fired" )
)
(defrule rule1590
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1590 was fired" )
)
(defrule rule1591
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1591 was fired" )
)
(defrule rule1592
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1592 was fired" )
)
(defrule rule1593
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1593 was fired" )
)
(defrule rule1594
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1594 was fired" )
)
(defrule rule1595
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1595 was fired" )
)
(defrule rule1596
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1596 was fired" )
)
(defrule rule1597
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1597 was fired" )
)
(defrule rule1598
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1598 was fired" )
)
(defrule rule1599
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1599 was fired" )
)
(defrule rule1600
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1600 was fired" )
)
(defrule rule1601
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1601 was fired" )
)
(defrule rule1602
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1602 was fired" )
)
(defrule rule1603
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1603 was fired" )
)
(defrule rule1604
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1604 was fired" )
)
(defrule rule1605
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1605 was fired" )
)
(defrule rule1606
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1606 was fired" )
)
(defrule rule1607
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1607 was fired" )
)
(defrule rule1608
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1608 was fired" )
)
(defrule rule1609
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1609 was fired" )
)
(defrule rule1610
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1610 was fired" )
)
(defrule rule1611
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1611 was fired" )
)
(defrule rule1612
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1612 was fired" )
)
(defrule rule1613
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1613 was fired" )
)
(defrule rule1614
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1614 was fired" )
)
(defrule rule1615
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1615 was fired" )
)
(defrule rule1616
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1616 was fired" )
)
(defrule rule1617
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1617 was fired" )
)
(defrule rule1618
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1618 was fired" )
)
(defrule rule1619
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1619 was fired" )
)
(defrule rule1620
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1620 was fired" )
)
(defrule rule1621
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1621 was fired" )
)
(defrule rule1622
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1622 was fired" )
)
(defrule rule1623
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1623 was fired" )
)
(defrule rule1624
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1624 was fired" )
)
(defrule rule1625
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1625 was fired" )
)
(defrule rule1626
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1626 was fired" )
)
(defrule rule1627
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1627 was fired" )
)
(defrule rule1628
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1628 was fired" )
)
(defrule rule1629
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1629 was fired" )
)
(defrule rule1630
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1630 was fired" )
)
(defrule rule1631
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1631 was fired" )
)
(defrule rule1632
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1632 was fired" )
)
(defrule rule1633
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1633 was fired" )
)
(defrule rule1634
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1634 was fired" )
)
(defrule rule1635
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1635 was fired" )
)
(defrule rule1636
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1636 was fired" )
)
(defrule rule1637
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1637 was fired" )
)
(defrule rule1638
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1638 was fired" )
)
(defrule rule1639
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1639 was fired" )
)
(defrule rule1640
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1640 was fired" )
)
(defrule rule1641
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1641 was fired" )
)
(defrule rule1642
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1642 was fired" )
)
(defrule rule1643
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1643 was fired" )
)
(defrule rule1644
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1644 was fired" )
)
(defrule rule1645
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1645 was fired" )
)
(defrule rule1646
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1646 was fired" )
)
(defrule rule1647
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1647 was fired" )
)
(defrule rule1648
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1648 was fired" )
)
(defrule rule1649
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1649 was fired" )
)
(defrule rule1650
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1650 was fired" )
)
(defrule rule1651
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1651 was fired" )
)
(defrule rule1652
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1652 was fired" )
)
(defrule rule1653
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1653 was fired" )
)
(defrule rule1654
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1654 was fired" )
)
(defrule rule1655
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1655 was fired" )
)
(defrule rule1656
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1656 was fired" )
)
(defrule rule1657
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1657 was fired" )
)
(defrule rule1658
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1658 was fired" )
)
(defrule rule1659
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1659 was fired" )
)
(defrule rule1660
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1660 was fired" )
)
(defrule rule1661
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1661 was fired" )
)
(defrule rule1662
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1662 was fired" )
)
(defrule rule1663
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1663 was fired" )
)
(defrule rule1664
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1664 was fired" )
)
(defrule rule1665
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1665 was fired" )
)
(defrule rule1666
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1666 was fired" )
)
(defrule rule1667
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1667 was fired" )
)
(defrule rule1668
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1668 was fired" )
)
(defrule rule1669
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1669 was fired" )
)
(defrule rule1670
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1670 was fired" )
)
(defrule rule1671
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1671 was fired" )
)
(defrule rule1672
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1672 was fired" )
)
(defrule rule1673
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1673 was fired" )
)
(defrule rule1674
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1674 was fired" )
)
(defrule rule1675
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1675 was fired" )
)
(defrule rule1676
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1676 was fired" )
)
(defrule rule1677
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1677 was fired" )
)
(defrule rule1678
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1678 was fired" )
)
(defrule rule1679
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1679 was fired" )
)
(defrule rule1680
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1680 was fired" )
)
(defrule rule1681
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1681 was fired" )
)
(defrule rule1682
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1682 was fired" )
)
(defrule rule1683
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1683 was fired" )
)
(defrule rule1684
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1684 was fired" )
)
(defrule rule1685
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1685 was fired" )
)
(defrule rule1686
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1686 was fired" )
)
(defrule rule1687
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1687 was fired" )
)
(defrule rule1688
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1688 was fired" )
)
(defrule rule1689
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1689 was fired" )
)
(defrule rule1690
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1690 was fired" )
)
(defrule rule1691
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1691 was fired" )
)
(defrule rule1692
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1692 was fired" )
)
(defrule rule1693
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1693 was fired" )
)
(defrule rule1694
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1694 was fired" )
)
(defrule rule1695
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1695 was fired" )
)
(defrule rule1696
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1696 was fired" )
)
(defrule rule1697
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1697 was fired" )
)
(defrule rule1698
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1698 was fired" )
)
(defrule rule1699
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1699 was fired" )
)
(defrule rule1700
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1700 was fired" )
)
(defrule rule1701
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1701 was fired" )
)
(defrule rule1702
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1702 was fired" )
)
(defrule rule1703
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1703 was fired" )
)
(defrule rule1704
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1704 was fired" )
)
(defrule rule1705
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1705 was fired" )
)
(defrule rule1706
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1706 was fired" )
)
(defrule rule1707
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1707 was fired" )
)
(defrule rule1708
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1708 was fired" )
)
(defrule rule1709
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1709 was fired" )
)
(defrule rule1710
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1710 was fired" )
)
(defrule rule1711
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1711 was fired" )
)
(defrule rule1712
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1712 was fired" )
)
(defrule rule1713
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1713 was fired" )
)
(defrule rule1714
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1714 was fired" )
)
(defrule rule1715
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1715 was fired" )
)
(defrule rule1716
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1716 was fired" )
)
(defrule rule1717
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1717 was fired" )
)
(defrule rule1718
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1718 was fired" )
)
(defrule rule1719
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1719 was fired" )
)
(defrule rule1720
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1720 was fired" )
)
(defrule rule1721
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1721 was fired" )
)
(defrule rule1722
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1722 was fired" )
)
(defrule rule1723
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1723 was fired" )
)
(defrule rule1724
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1724 was fired" )
)
(defrule rule1725
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1725 was fired" )
)
(defrule rule1726
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1726 was fired" )
)
(defrule rule1727
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1727 was fired" )
)
(defrule rule1728
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1728 was fired" )
)
(defrule rule1729
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1729 was fired" )
)
(defrule rule1730
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1730 was fired" )
)
(defrule rule1731
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1731 was fired" )
)
(defrule rule1732
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1732 was fired" )
)
(defrule rule1733
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1733 was fired" )
)
(defrule rule1734
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1734 was fired" )
)
(defrule rule1735
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1735 was fired" )
)
(defrule rule1736
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1736 was fired" )
)
(defrule rule1737
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1737 was fired" )
)
(defrule rule1738
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1738 was fired" )
)
(defrule rule1739
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1739 was fired" )
)
(defrule rule1740
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1740 was fired" )
)
(defrule rule1741
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1741 was fired" )
)
(defrule rule1742
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1742 was fired" )
)
(defrule rule1743
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1743 was fired" )
)
(defrule rule1744
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1744 was fired" )
)
(defrule rule1745
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1745 was fired" )
)
(defrule rule1746
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1746 was fired" )
)
(defrule rule1747
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1747 was fired" )
)
(defrule rule1748
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1748 was fired" )
)
(defrule rule1749
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1749 was fired" )
)
(defrule rule1750
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1750 was fired" )
)
(defrule rule1751
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1751 was fired" )
)
(defrule rule1752
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1752 was fired" )
)
(defrule rule1753
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1753 was fired" )
)
(defrule rule1754
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1754 was fired" )
)
(defrule rule1755
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1755 was fired" )
)
(defrule rule1756
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1756 was fired" )
)
(defrule rule1757
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1757 was fired" )
)
(defrule rule1758
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1758 was fired" )
)
(defrule rule1759
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1759 was fired" )
)
(defrule rule1760
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1760 was fired" )
)
(defrule rule1761
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1761 was fired" )
)
(defrule rule1762
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1762 was fired" )
)
(defrule rule1763
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1763 was fired" )
)
(defrule rule1764
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1764 was fired" )
)
(defrule rule1765
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1765 was fired" )
)
(defrule rule1766
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1766 was fired" )
)
(defrule rule1767
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1767 was fired" )
)
(defrule rule1768
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1768 was fired" )
)
(defrule rule1769
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1769 was fired" )
)
(defrule rule1770
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1770 was fired" )
)
(defrule rule1771
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1771 was fired" )
)
(defrule rule1772
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1772 was fired" )
)
(defrule rule1773
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1773 was fired" )
)
(defrule rule1774
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1774 was fired" )
)
(defrule rule1775
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1775 was fired" )
)
(defrule rule1776
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1776 was fired" )
)
(defrule rule1777
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1777 was fired" )
)
(defrule rule1778
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1778 was fired" )
)
(defrule rule1779
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1779 was fired" )
)
(defrule rule1780
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1780 was fired" )
)
(defrule rule1781
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1781 was fired" )
)
(defrule rule1782
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1782 was fired" )
)
(defrule rule1783
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1783 was fired" )
)
(defrule rule1784
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1784 was fired" )
)
(defrule rule1785
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1785 was fired" )
)
(defrule rule1786
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1786 was fired" )
)
(defrule rule1787
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1787 was fired" )
)
(defrule rule1788
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1788 was fired" )
)
(defrule rule1789
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1789 was fired" )
)
(defrule rule1790
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1790 was fired" )
)
(defrule rule1791
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1791 was fired" )
)
(defrule rule1792
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1792 was fired" )
)
(defrule rule1793
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1793 was fired" )
)
(defrule rule1794
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1794 was fired" )
)
(defrule rule1795
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1795 was fired" )
)
(defrule rule1796
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1796 was fired" )
)
(defrule rule1797
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1797 was fired" )
)
(defrule rule1798
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1798 was fired" )
)
(defrule rule1799
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1799 was fired" )
)
(defrule rule1800
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1800 was fired" )
)
(defrule rule1801
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1801 was fired" )
)
(defrule rule1802
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1802 was fired" )
)
(defrule rule1803
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1803 was fired" )
)
(defrule rule1804
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1804 was fired" )
)
(defrule rule1805
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1805 was fired" )
)
(defrule rule1806
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1806 was fired" )
)
(defrule rule1807
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1807 was fired" )
)
(defrule rule1808
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1808 was fired" )
)
(defrule rule1809
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1809 was fired" )
)
(defrule rule1810
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1810 was fired" )
)
(defrule rule1811
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1811 was fired" )
)
(defrule rule1812
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1812 was fired" )
)
(defrule rule1813
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1813 was fired" )
)
(defrule rule1814
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1814 was fired" )
)
(defrule rule1815
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1815 was fired" )
)
(defrule rule1816
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1816 was fired" )
)
(defrule rule1817
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1817 was fired" )
)
(defrule rule1818
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1818 was fired" )
)
(defrule rule1819
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1819 was fired" )
)
(defrule rule1820
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1820 was fired" )
)
(defrule rule1821
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1821 was fired" )
)
(defrule rule1822
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1822 was fired" )
)
(defrule rule1823
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1823 was fired" )
)
(defrule rule1824
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1824 was fired" )
)
(defrule rule1825
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1825 was fired" )
)
(defrule rule1826
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1826 was fired" )
)
(defrule rule1827
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1827 was fired" )
)
(defrule rule1828
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1828 was fired" )
)
(defrule rule1829
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1829 was fired" )
)
(defrule rule1830
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1830 was fired" )
)
(defrule rule1831
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1831 was fired" )
)
(defrule rule1832
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1832 was fired" )
)
(defrule rule1833
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1833 was fired" )
)
(defrule rule1834
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1834 was fired" )
)
(defrule rule1835
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1835 was fired" )
)
(defrule rule1836
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1836 was fired" )
)
(defrule rule1837
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1837 was fired" )
)
(defrule rule1838
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1838 was fired" )
)
(defrule rule1839
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1839 was fired" )
)
(defrule rule1840
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1840 was fired" )
)
(defrule rule1841
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1841 was fired" )
)
(defrule rule1842
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1842 was fired" )
)
(defrule rule1843
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1843 was fired" )
)
(defrule rule1844
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1844 was fired" )
)
(defrule rule1845
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1845 was fired" )
)
(defrule rule1846
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1846 was fired" )
)
(defrule rule1847
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1847 was fired" )
)
(defrule rule1848
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1848 was fired" )
)
(defrule rule1849
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1849 was fired" )
)
(defrule rule1850
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1850 was fired" )
)
(defrule rule1851
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1851 was fired" )
)
(defrule rule1852
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1852 was fired" )
)
(defrule rule1853
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1853 was fired" )
)
(defrule rule1854
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1854 was fired" )
)
(defrule rule1855
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1855 was fired" )
)
(defrule rule1856
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1856 was fired" )
)
(defrule rule1857
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1857 was fired" )
)
(defrule rule1858
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1858 was fired" )
)
(defrule rule1859
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1859 was fired" )
)
(defrule rule1860
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1860 was fired" )
)
(defrule rule1861
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1861 was fired" )
)
(defrule rule1862
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1862 was fired" )
)
(defrule rule1863
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1863 was fired" )
)
(defrule rule1864
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1864 was fired" )
)
(defrule rule1865
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1865 was fired" )
)
(defrule rule1866
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1866 was fired" )
)
(defrule rule1867
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1867 was fired" )
)
(defrule rule1868
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1868 was fired" )
)
(defrule rule1869
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1869 was fired" )
)
(defrule rule1870
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1870 was fired" )
)
(defrule rule1871
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1871 was fired" )
)
(defrule rule1872
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1872 was fired" )
)
(defrule rule1873
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1873 was fired" )
)
(defrule rule1874
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1874 was fired" )
)
(defrule rule1875
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1875 was fired" )
)
(defrule rule1876
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1876 was fired" )
)
(defrule rule1877
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1877 was fired" )
)
(defrule rule1878
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1878 was fired" )
)
(defrule rule1879
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1879 was fired" )
)
(defrule rule1880
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1880 was fired" )
)
(defrule rule1881
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1881 was fired" )
)
(defrule rule1882
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1882 was fired" )
)
(defrule rule1883
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1883 was fired" )
)
(defrule rule1884
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1884 was fired" )
)
(defrule rule1885
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1885 was fired" )
)
(defrule rule1886
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1886 was fired" )
)
(defrule rule1887
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1887 was fired" )
)
(defrule rule1888
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1888 was fired" )
)
(defrule rule1889
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1889 was fired" )
)
(defrule rule1890
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1890 was fired" )
)
(defrule rule1891
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1891 was fired" )
)
(defrule rule1892
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1892 was fired" )
)
(defrule rule1893
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1893 was fired" )
)
(defrule rule1894
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1894 was fired" )
)
(defrule rule1895
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1895 was fired" )
)
(defrule rule1896
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1896 was fired" )
)
(defrule rule1897
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1897 was fired" )
)
(defrule rule1898
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1898 was fired" )
)
(defrule rule1899
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1899 was fired" )
)
(defrule rule1900
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1900 was fired" )
)
(defrule rule1901
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1901 was fired" )
)
(defrule rule1902
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1902 was fired" )
)
(defrule rule1903
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1903 was fired" )
)
(defrule rule1904
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1904 was fired" )
)
(defrule rule1905
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1905 was fired" )
)
(defrule rule1906
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1906 was fired" )
)
(defrule rule1907
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1907 was fired" )
)
(defrule rule1908
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1908 was fired" )
)
(defrule rule1909
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1909 was fired" )
)
(defrule rule1910
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1910 was fired" )
)
(defrule rule1911
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1911 was fired" )
)
(defrule rule1912
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1912 was fired" )
)
(defrule rule1913
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1913 was fired" )
)
(defrule rule1914
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1914 was fired" )
)
(defrule rule1915
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1915 was fired" )
)
(defrule rule1916
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1916 was fired" )
)
(defrule rule1917
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1917 was fired" )
)
(defrule rule1918
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1918 was fired" )
)
(defrule rule1919
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1919 was fired" )
)
(defrule rule1920
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1920 was fired" )
)
(defrule rule1921
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1921 was fired" )
)
(defrule rule1922
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule1922 was fired" )
)
(defrule rule1923
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1923 was fired" )
)
(defrule rule1924
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1924 was fired" )
)
(defrule rule1925
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1925 was fired" )
)
(defrule rule1926
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1926 was fired" )
)
(defrule rule1927
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1927 was fired" )
)
(defrule rule1928
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1928 was fired" )
)
(defrule rule1929
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule1929 was fired" )
)
(defrule rule1930
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1930 was fired" )
)
(defrule rule1931
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1931 was fired" )
)
(defrule rule1932
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule1932 was fired" )
)
(defrule rule1933
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1933 was fired" )
)
(defrule rule1934
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1934 was fired" )
)
(defrule rule1935
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1935 was fired" )
)
(defrule rule1936
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1936 was fired" )
)
(defrule rule1937
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1937 was fired" )
)
(defrule rule1938
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1938 was fired" )
)
(defrule rule1939
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1939 was fired" )
)
(defrule rule1940
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule1940 was fired" )
)
(defrule rule1941
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1941 was fired" )
)
(defrule rule1942
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule1942 was fired" )
)
(defrule rule1943
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1943 was fired" )
)
(defrule rule1944
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1944 was fired" )
)
(defrule rule1945
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1945 was fired" )
)
(defrule rule1946
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1946 was fired" )
)
(defrule rule1947
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1947 was fired" )
)
(defrule rule1948
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1948 was fired" )
)
(defrule rule1949
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1949 was fired" )
)
(defrule rule1950
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1950 was fired" )
)
(defrule rule1951
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1951 was fired" )
)
(defrule rule1952
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule1952 was fired" )
)
(defrule rule1953
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule1953 was fired" )
)
(defrule rule1954
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1954 was fired" )
)
(defrule rule1955
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1955 was fired" )
)
(defrule rule1956
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1956 was fired" )
)
(defrule rule1957
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1957 was fired" )
)
(defrule rule1958
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule1958 was fired" )
)
(defrule rule1959
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1959 was fired" )
)
(defrule rule1960
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule1960 was fired" )
)
(defrule rule1961
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1961 was fired" )
)
(defrule rule1962
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1962 was fired" )
)
(defrule rule1963
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1963 was fired" )
)
(defrule rule1964
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule1964 was fired" )
)
(defrule rule1965
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1965 was fired" )
)
(defrule rule1966
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101040)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1966 was fired" )
)
(defrule rule1967
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule1967 was fired" )
)
(defrule rule1968
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1968 was fired" )
)
(defrule rule1969
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1969 was fired" )
)
(defrule rule1970
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1970 was fired" )
)
(defrule rule1971
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1971 was fired" )
)
(defrule rule1972
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule1972 was fired" )
)
(defrule rule1973
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule1973 was fired" )
)
(defrule rule1974
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1974 was fired" )
)
(defrule rule1975
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule1975 was fired" )
)
(defrule rule1976
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1976 was fired" )
)
(defrule rule1977
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1977 was fired" )
)
(defrule rule1978
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule1978 was fired" )
)
(defrule rule1979
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1979 was fired" )
)
(defrule rule1980
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1980 was fired" )
)
(defrule rule1981
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule1981 was fired" )
)
(defrule rule1982
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule1982 was fired" )
)
(defrule rule1983
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1983 was fired" )
)
(defrule rule1984
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1984 was fired" )
)
(defrule rule1985
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1985 was fired" )
)
(defrule rule1986
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1986 was fired" )
)
(defrule rule1987
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1987 was fired" )
)
(defrule rule1988
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule1988 was fired" )
)
(defrule rule1989
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule1989 was fired" )
)
(defrule rule1990
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule1990 was fired" )
)
(defrule rule1991
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule1991 was fired" )
)
(defrule rule1992
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1992 was fired" )
)
(defrule rule1993
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1993 was fired" )
)
(defrule rule1994
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule1994 was fired" )
)
(defrule rule1995
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule1995 was fired" )
)
(defrule rule1996
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule1996 was fired" )
)
(defrule rule1997
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule1997 was fired" )
)
(defrule rule1998
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule1998 was fired" )
)
(defrule rule1999
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule1999 was fired" )
)
