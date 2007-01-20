(deftemplate transaction
  (slot accountId)
  (slot buyPrice)
  (slot countryCode)
  (slot currentPrice)
  (slot cusip)
  (slot exchange)
  (slot industryGroupID)
  (slot industryID)
  (slot issuer)
  (slot lastPrice)
  (slot purchaseDate)
  (slot sectorID)
  (slot shares)
  (slot subIndustryID)
)
(defrule rule0
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (countryCode "US")
    (subIndustryID 25201010)
    (issuer "AAA")
    (exchange "NYSE")
  )
=>
  (printout t "rule0 was fired" )
)
(defrule rule1
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (countryCode "BR")
    (subIndustryID 25201020)
    (issuer "BBB")
    (exchange "NSDQ")
  )
=>
  (printout t "rule1 was fired" )
)
(defrule rule2
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (countryCode "FR")
    (subIndustryID 25201030)
    (issuer "CCC")
    (exchange "LNSE")
  )
=>
  (printout t "rule2 was fired" )
)
(defrule rule3
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (countryCode "NZ")
    (subIndustryID 25201040)
    (issuer "DDD")
    (exchange "TKYO")
  )
=>
  (printout t "rule3 was fired" )
)
(defrule rule4
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (countryCode "CA")
    (subIndustryID 25201050)
    (issuer "EEE")
    (exchange "TWSE")
  )
=>
  (printout t "rule4 was fired" )
)
