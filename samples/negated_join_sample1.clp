(deftemplate transaction
  (slot accountId (type STRING))
  (slot buyPrice (type DOUBLE))
  (slot countryCode (type STRING))
  (slot currentPrice (type DOUBLE))
  (slot cusip (type INTEGER))
  (slot exchange (type STRING))
  (slot industryGroupID (type INTEGER))
  (slot industryID (type INTEGER))
  (slot issuer (type STRING))
  (slot lastPrice (type DOUBLE))
  (slot purchaseDate (type STRING))
  (slot sectorID (type INTEGER))
  (slot shares (type DOUBLE))
  (slot subIndustryID (type INTEGER))
  (slot total (type DOUBLE))
)
(deftemplate account
  (slot accountId (type STRING))
  (slot cash (type DOUBLE))
  (slot fixedIncome (type DOUBLE))
  (slot stocks (type DOUBLE))
  (slot countryCode (type STRING))
)
(defrule joinrule1
 (transaction
    (accountId ?accid)
    (countryCode "US")
    (total ?tl&:(< ?tl 10000.00))
  )
  ( not
    (account
      (accountId ?accid)
      (cash ?c&:(> ?c 1000000))
    )
  )
=>
  (printout t "negated joinrule1 was fired" )
)

