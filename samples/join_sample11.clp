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
(deftemplate rating
  (slot cusip (type INTEGER))
  (slot issuer (type STRING))
)
(defrule joinrule2
 (transaction
    (accountId ?accid)
    (countryCode "US")
    (issuer ?iss)
  )
  (not 
    (rating
      (issuer ~?iss)
    )
  )
  (account
    (accountId ?accid)
  )
=>
  (printout t "joinrule2 was fired" )
)
(assert (transaction (accountId "acc1")(countryCode "US")(total 1298.00)(cusip 10101010)(issuer "BOB") ) )
(assert (account (accountId "acc1")(cash 1200000) ) )
(assert (rating (cusip 10101020)(issuer "BOB") ) )
