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
    (issuer "DDD")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule0 was fired" )
)
(defrule rule1
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1 was fired" )
)
(defrule rule2
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule2 was fired" )
)
(defrule rule3
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule3 was fired" )
)
(defrule rule4
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule4 was fired" )
)
(defrule rule5
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule5 was fired" )
)
(defrule rule6
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule6 was fired" )
)
(defrule rule7
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule7 was fired" )
)
(defrule rule8
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule8 was fired" )
)
(defrule rule9
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule9 was fired" )
)
(defrule rule10
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule10 was fired" )
)
(defrule rule11
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule11 was fired" )
)
(defrule rule12
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule12 was fired" )
)
(defrule rule13
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule13 was fired" )
)
(defrule rule14
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule14 was fired" )
)
(defrule rule15
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule15 was fired" )
)
(defrule rule16
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule16 was fired" )
)
(defrule rule17
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151010)
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
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule18 was fired" )
)
(defrule rule19
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule19 was fired" )
)
(defrule rule20
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule20 was fired" )
)
(defrule rule21
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule21 was fired" )
)
(defrule rule22
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule22 was fired" )
)
(defrule rule23
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule23 was fired" )
)
(defrule rule24
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule24 was fired" )
)
(defrule rule25
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule25 was fired" )
)
(defrule rule26
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule26 was fired" )
)
(defrule rule27
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule27 was fired" )
)
(defrule rule28
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule28 was fired" )
)
(defrule rule29
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule29 was fired" )
)
(defrule rule30
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule30 was fired" )
)
(defrule rule31
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule31 was fired" )
)
(defrule rule32
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule32 was fired" )
)
(defrule rule33
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule33 was fired" )
)
(defrule rule34
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule34 was fired" )
)
(defrule rule35
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule35 was fired" )
)
(defrule rule36
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule36 was fired" )
)
(defrule rule37
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule37 was fired" )
)
(defrule rule38
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule38 was fired" )
)
(defrule rule39
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule39 was fired" )
)
(defrule rule40
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule40 was fired" )
)
(defrule rule41
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule41 was fired" )
)
(defrule rule42
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule42 was fired" )
)
(defrule rule43
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule43 was fired" )
)
(defrule rule44
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule44 was fired" )
)
(defrule rule45
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule45 was fired" )
)
(defrule rule46
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule46 was fired" )
)
(defrule rule47
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule47 was fired" )
)
(defrule rule48
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule48 was fired" )
)
(defrule rule49
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule49 was fired" )
)
(defrule rule50
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule50 was fired" )
)
(defrule rule51
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule51 was fired" )
)
(defrule rule52
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule52 was fired" )
)
(defrule rule53
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule53 was fired" )
)
(defrule rule54
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule54 was fired" )
)
(defrule rule55
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule55 was fired" )
)
(defrule rule56
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule56 was fired" )
)
(defrule rule57
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule57 was fired" )
)
(defrule rule58
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule58 was fired" )
)
(defrule rule59
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule59 was fired" )
)
(defrule rule60
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule60 was fired" )
)
(defrule rule61
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule61 was fired" )
)
(defrule rule62
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201040)
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
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule63 was fired" )
)
(defrule rule64
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule64 was fired" )
)
(defrule rule65
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule65 was fired" )
)
(defrule rule66
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule66 was fired" )
)
(defrule rule67
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule67 was fired" )
)
(defrule rule68
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule68 was fired" )
)
(defrule rule69
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule69 was fired" )
)
(defrule rule70
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule70 was fired" )
)
(defrule rule71
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule71 was fired" )
)
(defrule rule72
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule72 was fired" )
)
(defrule rule73
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule73 was fired" )
)
(defrule rule74
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule74 was fired" )
)
(defrule rule75
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule75 was fired" )
)
(defrule rule76
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule76 was fired" )
)
(defrule rule77
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule77 was fired" )
)
(defrule rule78
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule78 was fired" )
)
(defrule rule79
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule79 was fired" )
)
(defrule rule80
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule80 was fired" )
)
(defrule rule81
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule81 was fired" )
)
(defrule rule82
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule82 was fired" )
)
(defrule rule83
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule83 was fired" )
)
(defrule rule84
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301010)
    (countryCode "ae")
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
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule85 was fired" )
)
(defrule rule86
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule86 was fired" )
)
(defrule rule87
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule87 was fired" )
)
(defrule rule88
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule88 was fired" )
)
(defrule rule89
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule89 was fired" )
)
(defrule rule90
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule90 was fired" )
)
(defrule rule91
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule91 was fired" )
)
(defrule rule92
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule92 was fired" )
)
(defrule rule93
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151030)
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
    (issuer "MMM")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule94 was fired" )
)
(defrule rule95
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule95 was fired" )
)
(defrule rule96
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101030)
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
    (issuer "MMM")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule97 was fired" )
)
(defrule rule98
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151020)
    (countryCode "ao")
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
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule99 was fired" )
)
