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
  (account
    (accountId ?accid)
    (cash ?c&:(> ?c 1000000))
  )
=>
  (printout t "joinrule1 was fired" )
)
(defrule joinrule2
 (transaction
    (accountId ?accid)
    (countryCode "BR")
    (total ?tl&:(< ?tl 10001.00))
  )
  (account
    (accountId ?accid)
    (cash ?c&:(> ?c 1000001))
  )
=>
  (printout t "joinrule2 was fired" )
)
(defrule joinrule3
 (transaction
    (accountId ?accid)
    (countryCode "FR")
    (total ?tl&:(< ?tl 10002.00))
  )
  (account
    (accountId ?accid)
    (cash ?c&:(> ?c 1000002))
  )
=>
  (printout t "joinrule3 was fired" )
)
(defrule joinrule4
 (transaction
    (accountId ?accid)
    (countryCode "CA")
    (total ?tl&:(< ?tl 10003.00))
  )
  (account
    (accountId ?accid)
    (cash ?c&:(> ?c 1000003))
  )
=>
  (printout t "joinrule4 was fired" )
)
(defrule joinrule5
 (transaction
    (accountId ?accid)
    (countryCode "JP")
    (total ?tl&:(< ?tl 10004.00))
  )
  (account
    (accountId ?accid)
    (cash ?c&:(> ?c 1000004))
  )
=>
  (printout t "joinrule5 was fired" )
)
