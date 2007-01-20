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
    (issuer "LLL")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule0 was fired" )
)
(defrule rule1
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1 was fired" )
)
(defrule rule2
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule2 was fired" )
)
(defrule rule3
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule3 was fired" )
)
(defrule rule4
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301010)
    (countryCode "ao")
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
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule5 was fired" )
)
(defrule rule6
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule6 was fired" )
)
(defrule rule7
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule7 was fired" )
)
(defrule rule8
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule8 was fired" )
)
(defrule rule9
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule9 was fired" )
)
(defrule rule10
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule10 was fired" )
)
(defrule rule11
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule11 was fired" )
)
(defrule rule12
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule12 was fired" )
)
(defrule rule13
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule13 was fired" )
)
(defrule rule14
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301030)
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
    (issuer "NNN")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule15 was fired" )
)
(defrule rule16
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule16 was fired" )
)
(defrule rule17
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule17 was fired" )
)
(defrule rule18
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule18 was fired" )
)
(defrule rule19
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule19 was fired" )
)
(defrule rule20
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule20 was fired" )
)
(defrule rule21
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule21 was fired" )
)
(defrule rule22
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule22 was fired" )
)
(defrule rule23
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule23 was fired" )
)
(defrule rule24
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule24 was fired" )
)
(defrule rule25
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule25 was fired" )
)
(defrule rule26
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule26 was fired" )
)
(defrule rule27
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule27 was fired" )
)
(defrule rule28
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule28 was fired" )
)
(defrule rule29
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151030)
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
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule30 was fired" )
)
(defrule rule31
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule31 was fired" )
)
(defrule rule32
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule32 was fired" )
)
(defrule rule33
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251040)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule33 was fired" )
)
(defrule rule34
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301020)
    (countryCode "ai")
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
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule35 was fired" )
)
(defrule rule36
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule36 was fired" )
)
(defrule rule37
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule37 was fired" )
)
(defrule rule38
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule38 was fired" )
)
(defrule rule39
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule39 was fired" )
)
(defrule rule40
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule40 was fired" )
)
(defrule rule41
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule41 was fired" )
)
(defrule rule42
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule42 was fired" )
)
(defrule rule43
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule43 was fired" )
)
(defrule rule44
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule44 was fired" )
)
(defrule rule45
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule45 was fired" )
)
(defrule rule46
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule46 was fired" )
)
(defrule rule47
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201040)
    (countryCode "ag")
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
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule48 was fired" )
)
(defrule rule49
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule49 was fired" )
)
(defrule rule50
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule50 was fired" )
)
(defrule rule51
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule51 was fired" )
)
(defrule rule52
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule52 was fired" )
)
(defrule rule53
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule53 was fired" )
)
(defrule rule54
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule54 was fired" )
)
(defrule rule55
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule55 was fired" )
)
(defrule rule56
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301020)
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
    (issuer "DDD")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule57 was fired" )
)
(defrule rule58
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule58 was fired" )
)
(defrule rule59
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301030)
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
    (issuer "RRR")
    (subIndustryID 10201020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule60 was fired" )
)
(defrule rule61
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule61 was fired" )
)
(defrule rule62
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule62 was fired" )
)
(defrule rule63
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule63 was fired" )
)
(defrule rule64
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule64 was fired" )
)
(defrule rule65
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule65 was fired" )
)
(defrule rule66
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule66 was fired" )
)
(defrule rule67
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule67 was fired" )
)
(defrule rule68
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251020)
    (countryCode "am")
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
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule69 was fired" )
)
(defrule rule70
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule70 was fired" )
)
(defrule rule71
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule71 was fired" )
)
(defrule rule72
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule72 was fired" )
)
(defrule rule73
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule73 was fired" )
)
(defrule rule74
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101010)
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
    (issuer "WWW")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule75 was fired" )
)
(defrule rule76
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule76 was fired" )
)
(defrule rule77
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule77 was fired" )
)
(defrule rule78
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule78 was fired" )
)
(defrule rule79
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule79 was fired" )
)
(defrule rule80
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule80 was fired" )
)
(defrule rule81
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule81 was fired" )
)
(defrule rule82
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule82 was fired" )
)
(defrule rule83
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule83 was fired" )
)
(defrule rule84
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule84 was fired" )
)
(defrule rule85
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule85 was fired" )
)
(defrule rule86
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule86 was fired" )
)
(defrule rule87
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule87 was fired" )
)
(defrule rule88
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule88 was fired" )
)
(defrule rule89
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule89 was fired" )
)
(defrule rule90
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule90 was fired" )
)
(defrule rule91
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule91 was fired" )
)
(defrule rule92
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule92 was fired" )
)
(defrule rule93
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule93 was fired" )
)
(defrule rule94
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule94 was fired" )
)
(defrule rule95
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151020)
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
    (issuer "III")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule96 was fired" )
)
(defrule rule97
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule97 was fired" )
)
(defrule rule98
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule98 was fired" )
)
(defrule rule99
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule99 was fired" )
)
(defrule rule100
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule100 was fired" )
)
(defrule rule101
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule101 was fired" )
)
(defrule rule102
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule102 was fired" )
)
(defrule rule103
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule103 was fired" )
)
(defrule rule104
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule104 was fired" )
)
(defrule rule105
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule105 was fired" )
)
(defrule rule106
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10301010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule106 was fired" )
)
(defrule rule107
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule107 was fired" )
)
(defrule rule108
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule108 was fired" )
)
(defrule rule109
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule109 was fired" )
)
(defrule rule110
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule110 was fired" )
)
(defrule rule111
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule111 was fired" )
)
(defrule rule112
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule112 was fired" )
)
(defrule rule113
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule113 was fired" )
)
(defrule rule114
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule114 was fired" )
)
(defrule rule115
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule115 was fired" )
)
(defrule rule116
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule116 was fired" )
)
(defrule rule117
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule117 was fired" )
)
(defrule rule118
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule118 was fired" )
)
(defrule rule119
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule119 was fired" )
)
(defrule rule120
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule120 was fired" )
)
(defrule rule121
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule121 was fired" )
)
(defrule rule122
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule122 was fired" )
)
(defrule rule123
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule123 was fired" )
)
(defrule rule124
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule124 was fired" )
)
(defrule rule125
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule125 was fired" )
)
(defrule rule126
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule126 was fired" )
)
(defrule rule127
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule127 was fired" )
)
(defrule rule128
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule128 was fired" )
)
(defrule rule129
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule129 was fired" )
)
(defrule rule130
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule130 was fired" )
)
(defrule rule131
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule131 was fired" )
)
(defrule rule132
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule132 was fired" )
)
(defrule rule133
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule133 was fired" )
)
(defrule rule134
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule134 was fired" )
)
(defrule rule135
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule135 was fired" )
)
(defrule rule136
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule136 was fired" )
)
(defrule rule137
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule137 was fired" )
)
(defrule rule138
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule138 was fired" )
)
(defrule rule139
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule139 was fired" )
)
(defrule rule140
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule140 was fired" )
)
(defrule rule141
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule141 was fired" )
)
(defrule rule142
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule142 was fired" )
)
(defrule rule143
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule143 was fired" )
)
(defrule rule144
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule144 was fired" )
)
(defrule rule145
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule145 was fired" )
)
(defrule rule146
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule146 was fired" )
)
(defrule rule147
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule147 was fired" )
)
(defrule rule148
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule148 was fired" )
)
(defrule rule149
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule149 was fired" )
)
(defrule rule150
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule150 was fired" )
)
(defrule rule151
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule151 was fired" )
)
(defrule rule152
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule152 was fired" )
)
(defrule rule153
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule153 was fired" )
)
(defrule rule154
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule154 was fired" )
)
(defrule rule155
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule155 was fired" )
)
(defrule rule156
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule156 was fired" )
)
(defrule rule157
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule157 was fired" )
)
(defrule rule158
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule158 was fired" )
)
(defrule rule159
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule159 was fired" )
)
(defrule rule160
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule160 was fired" )
)
(defrule rule161
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule161 was fired" )
)
(defrule rule162
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule162 was fired" )
)
(defrule rule163
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule163 was fired" )
)
(defrule rule164
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule164 was fired" )
)
(defrule rule165
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule165 was fired" )
)
(defrule rule166
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule166 was fired" )
)
(defrule rule167
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule167 was fired" )
)
(defrule rule168
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule168 was fired" )
)
(defrule rule169
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule169 was fired" )
)
(defrule rule170
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule170 was fired" )
)
(defrule rule171
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule171 was fired" )
)
(defrule rule172
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule172 was fired" )
)
(defrule rule173
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule173 was fired" )
)
(defrule rule174
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule174 was fired" )
)
(defrule rule175
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule175 was fired" )
)
(defrule rule176
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule176 was fired" )
)
(defrule rule177
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule177 was fired" )
)
(defrule rule178
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule178 was fired" )
)
(defrule rule179
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule179 was fired" )
)
(defrule rule180
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule180 was fired" )
)
(defrule rule181
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule181 was fired" )
)
(defrule rule182
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule182 was fired" )
)
(defrule rule183
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule183 was fired" )
)
(defrule rule184
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule184 was fired" )
)
(defrule rule185
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule185 was fired" )
)
(defrule rule186
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule186 was fired" )
)
(defrule rule187
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule187 was fired" )
)
(defrule rule188
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule188 was fired" )
)
(defrule rule189
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule189 was fired" )
)
(defrule rule190
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule190 was fired" )
)
(defrule rule191
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule191 was fired" )
)
(defrule rule192
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule192 was fired" )
)
(defrule rule193
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule193 was fired" )
)
(defrule rule194
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule194 was fired" )
)
(defrule rule195
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule195 was fired" )
)
(defrule rule196
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule196 was fired" )
)
(defrule rule197
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule197 was fired" )
)
(defrule rule198
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule198 was fired" )
)
(defrule rule199
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule199 was fired" )
)
(defrule rule200
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule200 was fired" )
)
(defrule rule201
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule201 was fired" )
)
(defrule rule202
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule202 was fired" )
)
(defrule rule203
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule203 was fired" )
)
(defrule rule204
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule204 was fired" )
)
(defrule rule205
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule205 was fired" )
)
(defrule rule206
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule206 was fired" )
)
(defrule rule207
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule207 was fired" )
)
(defrule rule208
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule208 was fired" )
)
(defrule rule209
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule209 was fired" )
)
(defrule rule210
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule210 was fired" )
)
(defrule rule211
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule211 was fired" )
)
(defrule rule212
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule212 was fired" )
)
(defrule rule213
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251010)
    (countryCode "af")
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
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule214 was fired" )
)
(defrule rule215
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule215 was fired" )
)
(defrule rule216
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule216 was fired" )
)
(defrule rule217
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule217 was fired" )
)
(defrule rule218
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule218 was fired" )
)
(defrule rule219
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule219 was fired" )
)
(defrule rule220
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule220 was fired" )
)
(defrule rule221
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule221 was fired" )
)
(defrule rule222
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule222 was fired" )
)
(defrule rule223
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule223 was fired" )
)
(defrule rule224
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule224 was fired" )
)
(defrule rule225
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule225 was fired" )
)
(defrule rule226
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule226 was fired" )
)
(defrule rule227
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule227 was fired" )
)
(defrule rule228
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule228 was fired" )
)
(defrule rule229
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule229 was fired" )
)
(defrule rule230
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule230 was fired" )
)
(defrule rule231
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule231 was fired" )
)
(defrule rule232
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule232 was fired" )
)
(defrule rule233
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule233 was fired" )
)
(defrule rule234
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule234 was fired" )
)
(defrule rule235
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule235 was fired" )
)
(defrule rule236
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule236 was fired" )
)
(defrule rule237
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule237 was fired" )
)
(defrule rule238
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule238 was fired" )
)
(defrule rule239
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule239 was fired" )
)
(defrule rule240
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule240 was fired" )
)
(defrule rule241
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule241 was fired" )
)
(defrule rule242
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule242 was fired" )
)
(defrule rule243
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule243 was fired" )
)
(defrule rule244
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule244 was fired" )
)
(defrule rule245
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule245 was fired" )
)
(defrule rule246
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule246 was fired" )
)
(defrule rule247
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule247 was fired" )
)
(defrule rule248
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule248 was fired" )
)
(defrule rule249
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule249 was fired" )
)
(defrule rule250
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule250 was fired" )
)
(defrule rule251
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule251 was fired" )
)
(defrule rule252
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule252 was fired" )
)
(defrule rule253
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule253 was fired" )
)
(defrule rule254
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule254 was fired" )
)
(defrule rule255
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule255 was fired" )
)
(defrule rule256
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule256 was fired" )
)
(defrule rule257
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule257 was fired" )
)
(defrule rule258
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule258 was fired" )
)
(defrule rule259
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule259 was fired" )
)
(defrule rule260
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule260 was fired" )
)
(defrule rule261
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule261 was fired" )
)
(defrule rule262
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule262 was fired" )
)
(defrule rule263
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule263 was fired" )
)
(defrule rule264
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule264 was fired" )
)
(defrule rule265
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule265 was fired" )
)
(defrule rule266
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule266 was fired" )
)
(defrule rule267
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule267 was fired" )
)
(defrule rule268
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule268 was fired" )
)
(defrule rule269
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule269 was fired" )
)
(defrule rule270
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule270 was fired" )
)
(defrule rule271
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule271 was fired" )
)
(defrule rule272
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule272 was fired" )
)
(defrule rule273
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule273 was fired" )
)
(defrule rule274
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule274 was fired" )
)
(defrule rule275
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule275 was fired" )
)
(defrule rule276
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule276 was fired" )
)
(defrule rule277
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule277 was fired" )
)
(defrule rule278
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule278 was fired" )
)
(defrule rule279
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule279 was fired" )
)
(defrule rule280
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule280 was fired" )
)
(defrule rule281
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule281 was fired" )
)
(defrule rule282
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule282 was fired" )
)
(defrule rule283
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule283 was fired" )
)
(defrule rule284
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule284 was fired" )
)
(defrule rule285
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule285 was fired" )
)
(defrule rule286
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule286 was fired" )
)
(defrule rule287
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule287 was fired" )
)
(defrule rule288
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "af")
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
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule289 was fired" )
)
(defrule rule290
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule290 was fired" )
)
(defrule rule291
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule291 was fired" )
)
(defrule rule292
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule292 was fired" )
)
(defrule rule293
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule293 was fired" )
)
(defrule rule294
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule294 was fired" )
)
(defrule rule295
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule295 was fired" )
)
(defrule rule296
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule296 was fired" )
)
(defrule rule297
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule297 was fired" )
)
(defrule rule298
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251020)
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
    (issuer "AAA")
    (subIndustryID 10151040)
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
    (issuer "EEE")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule300 was fired" )
)
(defrule rule301
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule301 was fired" )
)
(defrule rule302
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule302 was fired" )
)
(defrule rule303
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201020)
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
    (issuer "III")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule304 was fired" )
)
(defrule rule305
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule305 was fired" )
)
(defrule rule306
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201010)
    (countryCode "ae")
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
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule307 was fired" )
)
(defrule rule308
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151010)
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
    (issuer "QQQ")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule309 was fired" )
)
(defrule rule310
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule310 was fired" )
)
(defrule rule311
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule311 was fired" )
)
(defrule rule312
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule312 was fired" )
)
(defrule rule313
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule313 was fired" )
)
(defrule rule314
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule314 was fired" )
)
(defrule rule315
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule315 was fired" )
)
(defrule rule316
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule316 was fired" )
)
(defrule rule317
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule317 was fired" )
)
(defrule rule318
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151040)
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
    (issuer "NNN")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule319 was fired" )
)
(defrule rule320
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule320 was fired" )
)
(defrule rule321
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule321 was fired" )
)
(defrule rule322
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule322 was fired" )
)
(defrule rule323
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule323 was fired" )
)
(defrule rule324
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule324 was fired" )
)
(defrule rule325
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule325 was fired" )
)
(defrule rule326
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule326 was fired" )
)
(defrule rule327
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule327 was fired" )
)
(defrule rule328
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule328 was fired" )
)
(defrule rule329
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule329 was fired" )
)
(defrule rule330
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule330 was fired" )
)
(defrule rule331
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule331 was fired" )
)
(defrule rule332
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule332 was fired" )
)
(defrule rule333
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule333 was fired" )
)
(defrule rule334
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule334 was fired" )
)
(defrule rule335
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule335 was fired" )
)
(defrule rule336
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule336 was fired" )
)
(defrule rule337
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule337 was fired" )
)
(defrule rule338
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule338 was fired" )
)
(defrule rule339
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule339 was fired" )
)
(defrule rule340
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule340 was fired" )
)
(defrule rule341
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule341 was fired" )
)
(defrule rule342
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule342 was fired" )
)
(defrule rule343
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201010)
    (countryCode "ao")
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
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule344 was fired" )
)
(defrule rule345
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule345 was fired" )
)
(defrule rule346
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule346 was fired" )
)
(defrule rule347
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule347 was fired" )
)
(defrule rule348
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule348 was fired" )
)
(defrule rule349
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule349 was fired" )
)
(defrule rule350
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule350 was fired" )
)
(defrule rule351
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule351 was fired" )
)
(defrule rule352
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule352 was fired" )
)
(defrule rule353
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule353 was fired" )
)
(defrule rule354
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule354 was fired" )
)
(defrule rule355
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule355 was fired" )
)
(defrule rule356
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201030)
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
    (issuer "UUU")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule357 was fired" )
)
(defrule rule358
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule358 was fired" )
)
(defrule rule359
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule359 was fired" )
)
(defrule rule360
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule360 was fired" )
)
(defrule rule361
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule361 was fired" )
)
(defrule rule362
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule362 was fired" )
)
(defrule rule363
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule363 was fired" )
)
(defrule rule364
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule364 was fired" )
)
(defrule rule365
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule365 was fired" )
)
(defrule rule366
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule366 was fired" )
)
(defrule rule367
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule367 was fired" )
)
(defrule rule368
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule368 was fired" )
)
(defrule rule369
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule369 was fired" )
)
(defrule rule370
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule370 was fired" )
)
(defrule rule371
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule371 was fired" )
)
(defrule rule372
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule372 was fired" )
)
(defrule rule373
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule373 was fired" )
)
(defrule rule374
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201010)
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
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule375 was fired" )
)
(defrule rule376
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule376 was fired" )
)
(defrule rule377
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule377 was fired" )
)
(defrule rule378
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule378 was fired" )
)
(defrule rule379
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule379 was fired" )
)
(defrule rule380
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule380 was fired" )
)
(defrule rule381
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151030)
    (countryCode "al")
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
    (subIndustryID 10151020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule382 was fired" )
)
(defrule rule383
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule383 was fired" )
)
(defrule rule384
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule384 was fired" )
)
(defrule rule385
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule385 was fired" )
)
(defrule rule386
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule386 was fired" )
)
(defrule rule387
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule387 was fired" )
)
(defrule rule388
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule388 was fired" )
)
(defrule rule389
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule389 was fired" )
)
(defrule rule390
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule390 was fired" )
)
(defrule rule391
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule391 was fired" )
)
(defrule rule392
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule392 was fired" )
)
(defrule rule393
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule393 was fired" )
)
(defrule rule394
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule394 was fired" )
)
(defrule rule395
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule395 was fired" )
)
(defrule rule396
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule396 was fired" )
)
(defrule rule397
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule397 was fired" )
)
(defrule rule398
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151010)
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
    (issuer "EEE")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule399 was fired" )
)
(defrule rule400
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule400 was fired" )
)
(defrule rule401
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule401 was fired" )
)
(defrule rule402
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule402 was fired" )
)
(defrule rule403
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule403 was fired" )
)
(defrule rule404
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule404 was fired" )
)
(defrule rule405
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule405 was fired" )
)
(defrule rule406
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule406 was fired" )
)
(defrule rule407
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule407 was fired" )
)
(defrule rule408
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule408 was fired" )
)
(defrule rule409
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251040)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule409 was fired" )
)
(defrule rule410
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule410 was fired" )
)
(defrule rule411
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule411 was fired" )
)
(defrule rule412
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule412 was fired" )
)
(defrule rule413
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule413 was fired" )
)
(defrule rule414
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule414 was fired" )
)
(defrule rule415
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule415 was fired" )
)
(defrule rule416
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule416 was fired" )
)
(defrule rule417
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101010)
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
    (issuer "CCC")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule418 was fired" )
)
(defrule rule419
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule419 was fired" )
)
(defrule rule420
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201010)
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
    (issuer "SSS")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule421 was fired" )
)
(defrule rule422
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule422 was fired" )
)
(defrule rule423
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule423 was fired" )
)
(defrule rule424
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule424 was fired" )
)
(defrule rule425
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule425 was fired" )
)
(defrule rule426
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule426 was fired" )
)
(defrule rule427
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule427 was fired" )
)
(defrule rule428
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule428 was fired" )
)
(defrule rule429
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule429 was fired" )
)
(defrule rule430
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule430 was fired" )
)
(defrule rule431
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule431 was fired" )
)
(defrule rule432
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule432 was fired" )
)
(defrule rule433
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule433 was fired" )
)
(defrule rule434
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule434 was fired" )
)
(defrule rule435
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule435 was fired" )
)
(defrule rule436
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule436 was fired" )
)
(defrule rule437
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule437 was fired" )
)
(defrule rule438
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule438 was fired" )
)
(defrule rule439
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule439 was fired" )
)
(defrule rule440
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule440 was fired" )
)
(defrule rule441
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule441 was fired" )
)
(defrule rule442
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule442 was fired" )
)
(defrule rule443
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule443 was fired" )
)
(defrule rule444
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule444 was fired" )
)
(defrule rule445
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule445 was fired" )
)
(defrule rule446
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule446 was fired" )
)
(defrule rule447
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule447 was fired" )
)
(defrule rule448
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule448 was fired" )
)
(defrule rule449
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule449 was fired" )
)
(defrule rule450
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule450 was fired" )
)
(defrule rule451
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule451 was fired" )
)
(defrule rule452
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule452 was fired" )
)
(defrule rule453
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule453 was fired" )
)
(defrule rule454
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule454 was fired" )
)
(defrule rule455
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule455 was fired" )
)
(defrule rule456
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule456 was fired" )
)
(defrule rule457
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule457 was fired" )
)
(defrule rule458
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule458 was fired" )
)
(defrule rule459
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201030)
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
    (issuer "CCC")
    (subIndustryID 10201030)
    (countryCode "ag")
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
    (subIndustryID 10101020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule461 was fired" )
)
(defrule rule462
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule462 was fired" )
)
(defrule rule463
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule463 was fired" )
)
(defrule rule464
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule464 was fired" )
)
(defrule rule465
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule465 was fired" )
)
(defrule rule466
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule466 was fired" )
)
(defrule rule467
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule467 was fired" )
)
(defrule rule468
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule468 was fired" )
)
(defrule rule469
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule469 was fired" )
)
(defrule rule470
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule470 was fired" )
)
(defrule rule471
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule471 was fired" )
)
(defrule rule472
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule472 was fired" )
)
(defrule rule473
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule473 was fired" )
)
(defrule rule474
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule474 was fired" )
)
(defrule rule475
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule475 was fired" )
)
(defrule rule476
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule476 was fired" )
)
(defrule rule477
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule477 was fired" )
)
(defrule rule478
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule478 was fired" )
)
(defrule rule479
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule479 was fired" )
)
(defrule rule480
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
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
    (issuer "EEE")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule481 was fired" )
)
(defrule rule482
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule482 was fired" )
)
(defrule rule483
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule483 was fired" )
)
(defrule rule484
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule484 was fired" )
)
(defrule rule485
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule485 was fired" )
)
(defrule rule486
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule486 was fired" )
)
(defrule rule487
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule487 was fired" )
)
(defrule rule488
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101040)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule488 was fired" )
)
(defrule rule489
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule489 was fired" )
)
(defrule rule490
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule490 was fired" )
)
(defrule rule491
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule491 was fired" )
)
(defrule rule492
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule492 was fired" )
)
(defrule rule493
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule493 was fired" )
)
(defrule rule494
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule494 was fired" )
)
(defrule rule495
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule495 was fired" )
)
(defrule rule496
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101040)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule496 was fired" )
)
(defrule rule497
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251010)
    (countryCode "am")
    (exchange "NSDQ")
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
    (exchange "TKYO")
  )
=>
  (printout t "rule498 was fired" )
)
(defrule rule499
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule499 was fired" )
)
(defrule rule500
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule500 was fired" )
)
(defrule rule501
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule501 was fired" )
)
(defrule rule502
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule502 was fired" )
)
(defrule rule503
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule503 was fired" )
)
(defrule rule504
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule504 was fired" )
)
(defrule rule505
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule505 was fired" )
)
(defrule rule506
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule506 was fired" )
)
(defrule rule507
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule507 was fired" )
)
(defrule rule508
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule508 was fired" )
)
(defrule rule509
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule509 was fired" )
)
(defrule rule510
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule510 was fired" )
)
(defrule rule511
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule511 was fired" )
)
(defrule rule512
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule512 was fired" )
)
(defrule rule513
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule513 was fired" )
)
(defrule rule514
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule514 was fired" )
)
(defrule rule515
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule515 was fired" )
)
(defrule rule516
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule516 was fired" )
)
(defrule rule517
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule517 was fired" )
)
(defrule rule518
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule518 was fired" )
)
(defrule rule519
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule519 was fired" )
)
(defrule rule520
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule520 was fired" )
)
(defrule rule521
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule521 was fired" )
)
(defrule rule522
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule522 was fired" )
)
(defrule rule523
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule523 was fired" )
)
(defrule rule524
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule524 was fired" )
)
(defrule rule525
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule525 was fired" )
)
(defrule rule526
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule526 was fired" )
)
(defrule rule527
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule527 was fired" )
)
(defrule rule528
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule528 was fired" )
)
(defrule rule529
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule529 was fired" )
)
(defrule rule530
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule530 was fired" )
)
(defrule rule531
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule531 was fired" )
)
(defrule rule532
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule532 was fired" )
)
(defrule rule533
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule533 was fired" )
)
(defrule rule534
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule534 was fired" )
)
(defrule rule535
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule535 was fired" )
)
(defrule rule536
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule536 was fired" )
)
(defrule rule537
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule537 was fired" )
)
(defrule rule538
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule538 was fired" )
)
(defrule rule539
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule539 was fired" )
)
(defrule rule540
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule540 was fired" )
)
(defrule rule541
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule541 was fired" )
)
(defrule rule542
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule542 was fired" )
)
(defrule rule543
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule543 was fired" )
)
(defrule rule544
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule544 was fired" )
)
(defrule rule545
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule545 was fired" )
)
(defrule rule546
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule546 was fired" )
)
(defrule rule547
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule547 was fired" )
)
(defrule rule548
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule548 was fired" )
)
(defrule rule549
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule549 was fired" )
)
(defrule rule550
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule550 was fired" )
)
(defrule rule551
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule551 was fired" )
)
(defrule rule552
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule552 was fired" )
)
(defrule rule553
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule553 was fired" )
)
(defrule rule554
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule554 was fired" )
)
(defrule rule555
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301030)
    (countryCode "ae")
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
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule556 was fired" )
)
(defrule rule557
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule557 was fired" )
)
(defrule rule558
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule558 was fired" )
)
(defrule rule559
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule559 was fired" )
)
(defrule rule560
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule560 was fired" )
)
(defrule rule561
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule561 was fired" )
)
(defrule rule562
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule562 was fired" )
)
(defrule rule563
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule563 was fired" )
)
(defrule rule564
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule564 was fired" )
)
(defrule rule565
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule565 was fired" )
)
(defrule rule566
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule566 was fired" )
)
(defrule rule567
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule567 was fired" )
)
(defrule rule568
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule568 was fired" )
)
(defrule rule569
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule569 was fired" )
)
(defrule rule570
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule570 was fired" )
)
(defrule rule571
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule571 was fired" )
)
(defrule rule572
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule572 was fired" )
)
(defrule rule573
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule573 was fired" )
)
(defrule rule574
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule574 was fired" )
)
(defrule rule575
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule575 was fired" )
)
(defrule rule576
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule576 was fired" )
)
(defrule rule577
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule577 was fired" )
)
(defrule rule578
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule578 was fired" )
)
(defrule rule579
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule579 was fired" )
)
(defrule rule580
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule580 was fired" )
)
(defrule rule581
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule581 was fired" )
)
(defrule rule582
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule582 was fired" )
)
(defrule rule583
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule583 was fired" )
)
(defrule rule584
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule584 was fired" )
)
(defrule rule585
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule585 was fired" )
)
(defrule rule586
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule586 was fired" )
)
(defrule rule587
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule587 was fired" )
)
(defrule rule588
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule588 was fired" )
)
(defrule rule589
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule589 was fired" )
)
(defrule rule590
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule590 was fired" )
)
(defrule rule591
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule591 was fired" )
)
(defrule rule592
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule592 was fired" )
)
(defrule rule593
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule593 was fired" )
)
(defrule rule594
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule594 was fired" )
)
(defrule rule595
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule595 was fired" )
)
(defrule rule596
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule596 was fired" )
)
(defrule rule597
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule597 was fired" )
)
(defrule rule598
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule598 was fired" )
)
(defrule rule599
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule599 was fired" )
)
(defrule rule600
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule600 was fired" )
)
(defrule rule601
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule601 was fired" )
)
(defrule rule602
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule602 was fired" )
)
(defrule rule603
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule603 was fired" )
)
(defrule rule604
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule604 was fired" )
)
(defrule rule605
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule605 was fired" )
)
(defrule rule606
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule606 was fired" )
)
(defrule rule607
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule607 was fired" )
)
(defrule rule608
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule608 was fired" )
)
(defrule rule609
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule609 was fired" )
)
(defrule rule610
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule610 was fired" )
)
(defrule rule611
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule611 was fired" )
)
(defrule rule612
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule612 was fired" )
)
(defrule rule613
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule613 was fired" )
)
(defrule rule614
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule614 was fired" )
)
(defrule rule615
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule615 was fired" )
)
(defrule rule616
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule616 was fired" )
)
(defrule rule617
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule617 was fired" )
)
(defrule rule618
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule618 was fired" )
)
(defrule rule619
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule619 was fired" )
)
(defrule rule620
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule620 was fired" )
)
(defrule rule621
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule621 was fired" )
)
(defrule rule622
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule622 was fired" )
)
(defrule rule623
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule623 was fired" )
)
(defrule rule624
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule624 was fired" )
)
(defrule rule625
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule625 was fired" )
)
(defrule rule626
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule626 was fired" )
)
(defrule rule627
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule627 was fired" )
)
(defrule rule628
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule628 was fired" )
)
(defrule rule629
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule629 was fired" )
)
(defrule rule630
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule630 was fired" )
)
(defrule rule631
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule631 was fired" )
)
(defrule rule632
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule632 was fired" )
)
(defrule rule633
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule633 was fired" )
)
(defrule rule634
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule634 was fired" )
)
(defrule rule635
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule635 was fired" )
)
(defrule rule636
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule636 was fired" )
)
(defrule rule637
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule637 was fired" )
)
(defrule rule638
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule638 was fired" )
)
(defrule rule639
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule639 was fired" )
)
(defrule rule640
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule640 was fired" )
)
(defrule rule641
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule641 was fired" )
)
(defrule rule642
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule642 was fired" )
)
(defrule rule643
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule643 was fired" )
)
(defrule rule644
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule644 was fired" )
)
(defrule rule645
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule645 was fired" )
)
(defrule rule646
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301020)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule646 was fired" )
)
(defrule rule647
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule647 was fired" )
)
(defrule rule648
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule648 was fired" )
)
(defrule rule649
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule649 was fired" )
)
(defrule rule650
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule650 was fired" )
)
(defrule rule651
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule651 was fired" )
)
(defrule rule652
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule652 was fired" )
)
(defrule rule653
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule653 was fired" )
)
(defrule rule654
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule654 was fired" )
)
(defrule rule655
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule655 was fired" )
)
(defrule rule656
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule656 was fired" )
)
(defrule rule657
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10101010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule657 was fired" )
)
(defrule rule658
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule658 was fired" )
)
(defrule rule659
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule659 was fired" )
)
(defrule rule660
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule660 was fired" )
)
(defrule rule661
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule661 was fired" )
)
(defrule rule662
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule662 was fired" )
)
(defrule rule663
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule663 was fired" )
)
(defrule rule664
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule664 was fired" )
)
(defrule rule665
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule665 was fired" )
)
(defrule rule666
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule666 was fired" )
)
(defrule rule667
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule667 was fired" )
)
(defrule rule668
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule668 was fired" )
)
(defrule rule669
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule669 was fired" )
)
(defrule rule670
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule670 was fired" )
)
(defrule rule671
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule671 was fired" )
)
(defrule rule672
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251020)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule672 was fired" )
)
(defrule rule673
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule673 was fired" )
)
(defrule rule674
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule674 was fired" )
)
(defrule rule675
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201020)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule675 was fired" )
)
(defrule rule676
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301020)
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
    (issuer "SSS")
    (subIndustryID 10101040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule677 was fired" )
)
(defrule rule678
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule678 was fired" )
)
(defrule rule679
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule679 was fired" )
)
(defrule rule680
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule680 was fired" )
)
(defrule rule681
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule681 was fired" )
)
(defrule rule682
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule682 was fired" )
)
(defrule rule683
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule683 was fired" )
)
(defrule rule684
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule684 was fired" )
)
(defrule rule685
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule685 was fired" )
)
(defrule rule686
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule686 was fired" )
)
(defrule rule687
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule687 was fired" )
)
(defrule rule688
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule688 was fired" )
)
(defrule rule689
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule689 was fired" )
)
(defrule rule690
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule690 was fired" )
)
(defrule rule691
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule691 was fired" )
)
(defrule rule692
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule692 was fired" )
)
(defrule rule693
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule693 was fired" )
)
(defrule rule694
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule694 was fired" )
)
(defrule rule695
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule695 was fired" )
)
(defrule rule696
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule696 was fired" )
)
(defrule rule697
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule697 was fired" )
)
(defrule rule698
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule698 was fired" )
)
(defrule rule699
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule699 was fired" )
)
(defrule rule700
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule700 was fired" )
)
(defrule rule701
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule701 was fired" )
)
(defrule rule702
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule702 was fired" )
)
(defrule rule703
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule703 was fired" )
)
(defrule rule704
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule704 was fired" )
)
(defrule rule705
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule705 was fired" )
)
(defrule rule706
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule706 was fired" )
)
(defrule rule707
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201040)
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
    (issuer "III")
    (subIndustryID 10201030)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule708 was fired" )
)
(defrule rule709
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule709 was fired" )
)
(defrule rule710
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule710 was fired" )
)
(defrule rule711
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule711 was fired" )
)
(defrule rule712
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule712 was fired" )
)
(defrule rule713
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule713 was fired" )
)
(defrule rule714
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule714 was fired" )
)
(defrule rule715
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule715 was fired" )
)
(defrule rule716
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule716 was fired" )
)
(defrule rule717
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule717 was fired" )
)
(defrule rule718
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule718 was fired" )
)
(defrule rule719
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule719 was fired" )
)
(defrule rule720
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule720 was fired" )
)
(defrule rule721
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule721 was fired" )
)
(defrule rule722
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule722 was fired" )
)
(defrule rule723
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule723 was fired" )
)
(defrule rule724
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251040)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule724 was fired" )
)
(defrule rule725
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule725 was fired" )
)
(defrule rule726
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251030)
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
    (issuer "SSS")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule727 was fired" )
)
(defrule rule728
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101040)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule728 was fired" )
)
(defrule rule729
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule729 was fired" )
)
(defrule rule730
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
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
    (issuer "RRR")
    (subIndustryID 10251020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule731 was fired" )
)
(defrule rule732
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule732 was fired" )
)
(defrule rule733
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule733 was fired" )
)
(defrule rule734
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule734 was fired" )
)
(defrule rule735
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule735 was fired" )
)
(defrule rule736
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101020)
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
    (issuer "AAA")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule737 was fired" )
)
(defrule rule738
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301020)
    (countryCode "ag")
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
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule739 was fired" )
)
(defrule rule740
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule740 was fired" )
)
(defrule rule741
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule741 was fired" )
)
(defrule rule742
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule742 was fired" )
)
(defrule rule743
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201040)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule743 was fired" )
)
(defrule rule744
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule744 was fired" )
)
(defrule rule745
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule745 was fired" )
)
(defrule rule746
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule746 was fired" )
)
(defrule rule747
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule747 was fired" )
)
(defrule rule748
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101040)
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
    (issuer "EEE")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule749 was fired" )
)
(defrule rule750
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule750 was fired" )
)
(defrule rule751
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151040)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule751 was fired" )
)
(defrule rule752
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule752 was fired" )
)
(defrule rule753
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151010)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule753 was fired" )
)
(defrule rule754
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule754 was fired" )
)
(defrule rule755
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule755 was fired" )
)
(defrule rule756
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule756 was fired" )
)
(defrule rule757
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule757 was fired" )
)
(defrule rule758
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule758 was fired" )
)
(defrule rule759
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule759 was fired" )
)
(defrule rule760
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule760 was fired" )
)
(defrule rule761
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule761 was fired" )
)
(defrule rule762
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule762 was fired" )
)
(defrule rule763
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule763 was fired" )
)
(defrule rule764
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule764 was fired" )
)
(defrule rule765
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101040)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule765 was fired" )
)
(defrule rule766
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151040)
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
    (issuer "EEE")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule767 was fired" )
)
(defrule rule768
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule768 was fired" )
)
(defrule rule769
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule769 was fired" )
)
(defrule rule770
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule770 was fired" )
)
(defrule rule771
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule771 was fired" )
)
(defrule rule772
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule772 was fired" )
)
(defrule rule773
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151020)
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
    (issuer "KKK")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule774 was fired" )
)
(defrule rule775
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251030)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule775 was fired" )
)
(defrule rule776
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule776 was fired" )
)
(defrule rule777
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule777 was fired" )
)
(defrule rule778
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule778 was fired" )
)
(defrule rule779
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule779 was fired" )
)
(defrule rule780
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10251030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule780 was fired" )
)
(defrule rule781
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule781 was fired" )
)
(defrule rule782
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule782 was fired" )
)
(defrule rule783
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule783 was fired" )
)
(defrule rule784
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule784 was fired" )
)
(defrule rule785
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule785 was fired" )
)
(defrule rule786
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule786 was fired" )
)
(defrule rule787
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule787 was fired" )
)
(defrule rule788
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule788 was fired" )
)
(defrule rule789
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10151030)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule789 was fired" )
)
(defrule rule790
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule790 was fired" )
)
(defrule rule791
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule791 was fired" )
)
(defrule rule792
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule792 was fired" )
)
(defrule rule793
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule793 was fired" )
)
(defrule rule794
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule794 was fired" )
)
(defrule rule795
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule795 was fired" )
)
(defrule rule796
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule796 was fired" )
)
(defrule rule797
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule797 was fired" )
)
(defrule rule798
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule798 was fired" )
)
(defrule rule799
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10201040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule799 was fired" )
)
(defrule rule800
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule800 was fired" )
)
(defrule rule801
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule801 was fired" )
)
(defrule rule802
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule802 was fired" )
)
(defrule rule803
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule803 was fired" )
)
(defrule rule804
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule804 was fired" )
)
(defrule rule805
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule805 was fired" )
)
(defrule rule806
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule806 was fired" )
)
(defrule rule807
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule807 was fired" )
)
(defrule rule808
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule808 was fired" )
)
(defrule rule809
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule809 was fired" )
)
(defrule rule810
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101020)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule810 was fired" )
)
(defrule rule811
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10151020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule811 was fired" )
)
(defrule rule812
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule812 was fired" )
)
(defrule rule813
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule813 was fired" )
)
(defrule rule814
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule814 was fired" )
)
(defrule rule815
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10251040)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule815 was fired" )
)
(defrule rule816
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule816 was fired" )
)
(defrule rule817
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule817 was fired" )
)
(defrule rule818
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule818 was fired" )
)
(defrule rule819
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10301020)
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
    (issuer "SSS")
    (subIndustryID 10251030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule820 was fired" )
)
(defrule rule821
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule821 was fired" )
)
(defrule rule822
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201010)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule822 was fired" )
)
(defrule rule823
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10201030)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule823 was fired" )
)
(defrule rule824
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule824 was fired" )
)
(defrule rule825
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101040)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule825 was fired" )
)
(defrule rule826
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule826 was fired" )
)
(defrule rule827
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule827 was fired" )
)
(defrule rule828
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10301030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule828 was fired" )
)
(defrule rule829
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201020)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule829 was fired" )
)
(defrule rule830
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101020)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule830 was fired" )
)
(defrule rule831
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251010)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule831 was fired" )
)
(defrule rule832
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule832 was fired" )
)
(defrule rule833
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule833 was fired" )
)
(defrule rule834
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule834 was fired" )
)
(defrule rule835
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "NYSE")
  )
=>
  (printout t "rule835 was fired" )
)
(defrule rule836
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule836 was fired" )
)
(defrule rule837
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule837 was fired" )
)
(defrule rule838
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule838 was fired" )
)
(defrule rule839
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule839 was fired" )
)
(defrule rule840
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule840 was fired" )
)
(defrule rule841
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule841 was fired" )
)
(defrule rule842
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule842 was fired" )
)
(defrule rule843
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151030)
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
    (issuer "DDD")
    (subIndustryID 10301020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule844 was fired" )
)
(defrule rule845
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule845 was fired" )
)
(defrule rule846
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule846 was fired" )
)
(defrule rule847
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule847 was fired" )
)
(defrule rule848
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule848 was fired" )
)
(defrule rule849
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule849 was fired" )
)
(defrule rule850
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule850 was fired" )
)
(defrule rule851
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule851 was fired" )
)
(defrule rule852
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule852 was fired" )
)
(defrule rule853
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule853 was fired" )
)
(defrule rule854
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10101030)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule854 was fired" )
)
(defrule rule855
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule855 was fired" )
)
(defrule rule856
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule856 was fired" )
)
(defrule rule857
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule857 was fired" )
)
(defrule rule858
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151030)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule858 was fired" )
)
(defrule rule859
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10301030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule859 was fired" )
)
(defrule rule860
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule860 was fired" )
)
(defrule rule861
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201030)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule861 was fired" )
)
(defrule rule862
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule862 was fired" )
)
(defrule rule863
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10151010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule863 was fired" )
)
(defrule rule864
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule864 was fired" )
)
(defrule rule865
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule865 was fired" )
)
(defrule rule866
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "LNSE")
  )
=>
  (printout t "rule866 was fired" )
)
(defrule rule867
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule867 was fired" )
)
(defrule rule868
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251020)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule868 was fired" )
)
(defrule rule869
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule869 was fired" )
)
(defrule rule870
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule870 was fired" )
)
(defrule rule871
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251010)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule871 was fired" )
)
(defrule rule872
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule872 was fired" )
)
(defrule rule873
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule873 was fired" )
)
(defrule rule874
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10151020)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule874 was fired" )
)
(defrule rule875
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule875 was fired" )
)
(defrule rule876
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule876 was fired" )
)
(defrule rule877
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule877 was fired" )
)
(defrule rule878
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule878 was fired" )
)
(defrule rule879
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10201040)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule879 was fired" )
)
(defrule rule880
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10251020)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule880 was fired" )
)
(defrule rule881
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule881 was fired" )
)
(defrule rule882
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule882 was fired" )
)
(defrule rule883
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule883 was fired" )
)
(defrule rule884
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10301020)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule884 was fired" )
)
(defrule rule885
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201020)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule885 was fired" )
)
(defrule rule886
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule886 was fired" )
)
(defrule rule887
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101020)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule887 was fired" )
)
(defrule rule888
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "JJJ")
    (subIndustryID 10101030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule888 was fired" )
)
(defrule rule889
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule889 was fired" )
)
(defrule rule890
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10301030)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule890 was fired" )
)
(defrule rule891
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule891 was fired" )
)
(defrule rule892
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151020)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule892 was fired" )
)
(defrule rule893
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "HHH")
    (subIndustryID 10251030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule893 was fired" )
)
(defrule rule894
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule894 was fired" )
)
(defrule rule895
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201010)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule895 was fired" )
)
(defrule rule896
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule896 was fired" )
)
(defrule rule897
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101030)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule897 was fired" )
)
(defrule rule898
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151010)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule898 was fired" )
)
(defrule rule899
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule899 was fired" )
)
(defrule rule900
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201020)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule900 was fired" )
)
(defrule rule901
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301020)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule901 was fired" )
)
(defrule rule902
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251040)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule902 was fired" )
)
(defrule rule903
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule903 was fired" )
)
(defrule rule904
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule904 was fired" )
)
(defrule rule905
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "EEE")
    (subIndustryID 10101010)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule905 was fired" )
)
(defrule rule906
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule906 was fired" )
)
(defrule rule907
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule907 was fired" )
)
(defrule rule908
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301020)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule908 was fired" )
)
(defrule rule909
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10301010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule909 was fired" )
)
(defrule rule910
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (subIndustryID 10201020)
    (countryCode "al")
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
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule911 was fired" )
)
(defrule rule912
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10101010)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule912 was fired" )
)
(defrule rule913
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule913 was fired" )
)
(defrule rule914
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10151030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule914 was fired" )
)
(defrule rule915
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule915 was fired" )
)
(defrule rule916
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10251040)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule916 was fired" )
)
(defrule rule917
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "WWW")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule917 was fired" )
)
(defrule rule918
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule918 was fired" )
)
(defrule rule919
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10101010)
    (countryCode "al")
    (exchange "NSDQ")
  )
=>
  (printout t "rule919 was fired" )
)
(defrule rule920
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule920 was fired" )
)
(defrule rule921
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301010)
    (countryCode "an")
    (exchange "TKYO")
  )
=>
  (printout t "rule921 was fired" )
)
(defrule rule922
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule922 was fired" )
)
(defrule rule923
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101010)
    (countryCode "an")
    (exchange "NSDQ")
  )
=>
  (printout t "rule923 was fired" )
)
(defrule rule924
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10201030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule924 was fired" )
)
(defrule rule925
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule925 was fired" )
)
(defrule rule926
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10201040)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule926 was fired" )
)
(defrule rule927
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10301020)
    (countryCode "ai")
    (exchange "NYSE")
  )
=>
  (printout t "rule927 was fired" )
)
(defrule rule928
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "TKYO")
  )
=>
  (printout t "rule928 was fired" )
)
(defrule rule929
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule929 was fired" )
)
(defrule rule930
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule930 was fired" )
)
(defrule rule931
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule931 was fired" )
)
(defrule rule932
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10101010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule932 was fired" )
)
(defrule rule933
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10101020)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule933 was fired" )
)
(defrule rule934
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10201020)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule934 was fired" )
)
(defrule rule935
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule935 was fired" )
)
(defrule rule936
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201010)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule936 was fired" )
)
(defrule rule937
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule937 was fired" )
)
(defrule rule938
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10201020)
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
    (issuer "BBB")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "NSDQ")
  )
=>
  (printout t "rule939 was fired" )
)
(defrule rule940
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule940 was fired" )
)
(defrule rule941
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151020)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule941 was fired" )
)
(defrule rule942
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251040)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule942 was fired" )
)
(defrule rule943
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10301010)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule943 was fired" )
)
(defrule rule944
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule944 was fired" )
)
(defrule rule945
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10101020)
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
    (issuer "RRR")
    (subIndustryID 10251010)
    (countryCode "ad")
    (exchange "TKYO")
  )
=>
  (printout t "rule946 was fired" )
)
(defrule rule947
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301010)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule947 was fired" )
)
(defrule rule948
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10251040)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule948 was fired" )
)
(defrule rule949
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "MMM")
    (subIndustryID 10201040)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule949 was fired" )
)
(defrule rule950
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301030)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule950 was fired" )
)
(defrule rule951
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule951 was fired" )
)
(defrule rule952
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "LNSE")
  )
=>
  (printout t "rule952 was fired" )
)
(defrule rule953
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule953 was fired" )
)
(defrule rule954
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule954 was fired" )
)
(defrule rule955
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "QQQ")
    (subIndustryID 10301020)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule955 was fired" )
)
(defrule rule956
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201010)
    (countryCode "ag")
    (exchange "NSDQ")
  )
=>
  (printout t "rule956 was fired" )
)
(defrule rule957
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201010)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule957 was fired" )
)
(defrule rule958
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251020)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule958 was fired" )
)
(defrule rule959
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10201040)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule959 was fired" )
)
(defrule rule960
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "AAA")
    (subIndustryID 10101040)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule960 was fired" )
)
(defrule rule961
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule961 was fired" )
)
(defrule rule962
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule962 was fired" )
)
(defrule rule963
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251030)
    (countryCode "af")
    (exchange "LNSE")
  )
=>
  (printout t "rule963 was fired" )
)
(defrule rule964
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101040)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule964 was fired" )
)
(defrule rule965
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule965 was fired" )
)
(defrule rule966
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10101040)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule966 was fired" )
)
(defrule rule967
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10201020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule967 was fired" )
)
(defrule rule968
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10101030)
    (countryCode "al")
    (exchange "NYSE")
  )
=>
  (printout t "rule968 was fired" )
)
(defrule rule969
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10151040)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule969 was fired" )
)
(defrule rule970
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10151030)
    (countryCode "am")
    (exchange "TKYO")
  )
=>
  (printout t "rule970 was fired" )
)
(defrule rule971
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule971 was fired" )
)
(defrule rule972
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "OOO")
    (subIndustryID 10301020)
    (countryCode "am")
    (exchange "NSDQ")
  )
=>
  (printout t "rule972 was fired" )
)
(defrule rule973
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10101010)
    (countryCode "ad")
    (exchange "NSDQ")
  )
=>
  (printout t "rule973 was fired" )
)
(defrule rule974
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10301030)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule974 was fired" )
)
(defrule rule975
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "LNSE")
  )
=>
  (printout t "rule975 was fired" )
)
(defrule rule976
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10301030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule976 was fired" )
)
(defrule rule977
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule977 was fired" )
)
(defrule rule978
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "PPP")
    (subIndustryID 10201010)
    (countryCode "ao")
    (exchange "NSDQ")
  )
=>
  (printout t "rule978 was fired" )
)
(defrule rule979
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "LLL")
    (subIndustryID 10251020)
    (countryCode "al")
    (exchange "LNSE")
  )
=>
  (printout t "rule979 was fired" )
)
(defrule rule980
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10201030)
    (countryCode "af")
    (exchange "TKYO")
  )
=>
  (printout t "rule980 was fired" )
)
(defrule rule981
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "UUU")
    (subIndustryID 10151040)
    (countryCode "ao")
    (exchange "NYSE")
  )
=>
  (printout t "rule981 was fired" )
)
(defrule rule982
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "ae")
    (exchange "TKYO")
  )
=>
  (printout t "rule982 was fired" )
)
(defrule rule983
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "KKK")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "NYSE")
  )
=>
  (printout t "rule983 was fired" )
)
(defrule rule984
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10251010)
    (countryCode "af")
    (exchange "NSDQ")
  )
=>
  (printout t "rule984 was fired" )
)
(defrule rule985
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "III")
    (subIndustryID 10151040)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule985 was fired" )
)
(defrule rule986
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151020)
    (countryCode "ad")
    (exchange "LNSE")
  )
=>
  (printout t "rule986 was fired" )
)
(defrule rule987
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10301030)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule987 was fired" )
)
(defrule rule988
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10151010)
    (countryCode "ag")
    (exchange "LNSE")
  )
=>
  (printout t "rule988 was fired" )
)
(defrule rule989
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "RRR")
    (subIndustryID 10151040)
    (countryCode "am")
    (exchange "NYSE")
  )
=>
  (printout t "rule989 was fired" )
)
(defrule rule990
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "VVV")
    (subIndustryID 10251010)
    (countryCode "ag")
    (exchange "TKYO")
  )
=>
  (printout t "rule990 was fired" )
)
(defrule rule991
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10201020)
    (countryCode "an")
    (exchange "NYSE")
  )
=>
  (printout t "rule991 was fired" )
)
(defrule rule992
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10301010)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule992 was fired" )
)
(defrule rule993
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "FFF")
    (subIndustryID 10151010)
    (countryCode "ae")
    (exchange "LNSE")
  )
=>
  (printout t "rule993 was fired" )
)
(defrule rule994
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "TTT")
    (subIndustryID 10251020)
    (countryCode "ae")
    (exchange "NYSE")
  )
=>
  (printout t "rule994 was fired" )
)
(defrule rule995
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "NNN")
    (subIndustryID 10301010)
    (countryCode "al")
    (exchange "TKYO")
  )
=>
  (printout t "rule995 was fired" )
)
(defrule rule996
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "DDD")
    (subIndustryID 10251030)
    (countryCode "ao")
    (exchange "TKYO")
  )
=>
  (printout t "rule996 was fired" )
)
(defrule rule997
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "CCC")
    (subIndustryID 10101030)
    (countryCode "af")
    (exchange "NYSE")
  )
=>
  (printout t "rule997 was fired" )
)
(defrule rule998
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "SSS")
    (subIndustryID 10251030)
    (countryCode "an")
    (exchange "LNSE")
  )
=>
  (printout t "rule998 was fired" )
)
(defrule rule999
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "GGG")
    (subIndustryID 10101030)
    (countryCode "ai")
    (exchange "NSDQ")
  )
=>
  (printout t "rule999 was fired" )
)
