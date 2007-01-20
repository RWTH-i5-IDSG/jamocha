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
    (issuer "BBB")
    (exchange "NYSE")
    (countryCode "US")
    (subIndustryID 25201010)
  )
=>
  (printout t "rule0 was fired" )
)
(defrule rule1
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (exchange "NSDQ")
    (countryCode "BR")
    (subIndustryID 25201020)
  )
=>
  (printout t "rule1 was fired" )
)
(defrule rule2
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (exchange "NSDQ")
    (countryCode "FR")
    (subIndustryID 25201030)
  )
=>
  (printout t "rule2 was fired" )
)
(defrule rule3
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (exchange "NSDQ")
    (countryCode "NZ")
    (subIndustryID 25201040)
  )
=>
  (printout t "rule3 was fired" )
)

(defrule rule4
 (transaction
    (accountId ?accid)
    (buyPrice ?bp)
    (issuer "BBB")
    (exchange "TWSE")
    (countryCode "CA")
    (subIndustryID 25201050)
  )
=>
  (printout t "rule4 was fired" )
)