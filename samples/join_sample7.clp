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
(defrule joinrule2 (declare (salience 100)(rule-version a1.1) )
 (transaction
    (countryCode "US")
  )
  (rating
    (issuer "BOB")
  )
=>
  (printout t "joinrule2 was fired" crlf)
)
(defrule joinrule1 (declare (salience 10)(rule-version a1.0) )
 (transaction
    (countryCode "US")
  )
  (account
    (accountId "acc1")
  )
  (rating
    (issuer "BOB")
  )
=>
  (printout t "joinrule1 was fired" crlf)
)
(assert (transaction (accountId "acc1")(countryCode "US")(total 1298.00)(cusip 10101010)(issuer "BOB") ) )
(assert (account (accountId "acc1")(cash 1200000) ) )
(assert (rating (cusip 10101010)(issuer "BOB") ) )