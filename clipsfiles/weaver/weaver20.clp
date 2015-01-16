

(defrule p233
  ?f1 <- (context (present loose-constraint))
  (vertical (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?com1)) (grid-y ?gy))
  ?f4 <- (horizontal (net-name nil) (min ?min2&:(<= ?min2 ?gx2)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?com1))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (>= ?com2 ?gy))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (min-net ?hnn))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?com1)) (max ?qw63&:(>= ?qw63 ?com1))))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?qz47&:(and (< ?qz47 ?com2) (>= ?qz47 ?min1))) (layer ?lay)))
  (not (vertical (net-name nil) (max ?min1) (com ?com1)))
  (not (horizontal (net-name nil) (min ?qz22&:(< ?qz22 ?com1)) (max ?qw64&:(>= ?qw64 ?com1)) (com ?com2) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-x ?com1)))
  (not (vertical (status nil) (net-name ?nn&~nil) (max ?min1) (com ?com1) (layer ?lay)))
  =>
  (assert (horizontal (min ?com1) (max ?max2) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (min-net ?nn)))
  (modify ?f4 (max =(- ?com1 1)) (max-net ?nn))
  (assert (horizontal (max ?com1) (min =(- ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-x =(- ?com1 1)) (grid-y ?com2) (grid-layer ?lay) (came-from east)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p234
  ?f1 <- (context (present loose-constraint))
  (vertical (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?com1)) (grid-y ?gy))
  ?f4 <- (horizontal (net-name nil) (min ?min2&:(<= ?min2 ?gx2)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?com1))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (<= ?com2 ?gy))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (min-net ?hnn))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?com1)) (max ?qw63&:(>= ?qw63 ?com1))))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?qz30&:(and (> ?qz30 ?com2) (<= ?qz30 ?max1))) (layer ?lay)))
  (not (vertical (net-name nil) (max ?min1) (com ?com1)))
  (not (horizontal (net-name nil) (min ?qz22&:(< ?qz22 ?com1)) (max ?qw64&:(>= ?qw64 ?com1)) (com ?com2) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-x ?com1)))
  (not (vertical (status nil) (net-name ?nn&~nil) (max ?min1) (com ?com1) (layer ?lay)))
  =>
  (assert (horizontal (min ?com1) (max ?max2) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (min-net ?nn)))
  (modify ?f4 (max =(- ?com1 1)) (max-net ?nn))
  (assert (horizontal (max ?com1) (min =(- ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-x =(- ?com1 1)) (grid-y ?com2) (grid-layer ?lay) (came-from east)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p235
  ?f1 <- (context (present loose-constraint))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy2&:(> ?gy2 ?com1)))
  ?f4 <- (vertical (net-name nil) (min ?min2&:(<= ?min2 ?com1)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?gy2))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (>= ?com2 ?gx))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (min-net ?hnn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw87&:(> ?qw87 ?com1))))
  (not (vertical (net-name nil) (min ?qw75&:(<= ?qw75 ?com1)) (max ?qw88&:(> ?qw88 ?com1)) (com ?qz47&:(and (< ?qz47 ?com2) (>= ?qz47 ?min1))) (layer ?lay)))
  (not (horizontal (net-name nil) (max ?min1) (com ?com1)))
  (not (vertical (net-name nil) (min ?qw76&:(<= ?qw76 ?com1)) (max ?qw86&:(> ?qw86 ?com1)) (com ?com2) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-y ?com1)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (max ?min1) (com ?com1) (layer ?lay)))
  =>
  (assert (vertical (min ?min2) (max ?com1) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (min-net ?hnn) (max-net ?nn)))
  (modify ?f4 (min =(+ ?com1 1)) (min-net ?nn))
  (assert (vertical (min ?com1) (max =(+ ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-y =(+ ?com1 1)) (grid-x ?com2) (grid-layer ?lay) (came-from south)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p236
  ?f1 <- (context (present loose-constraint))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy2&:(> ?gy2 ?com1)))
  ?f4 <- (vertical (net-name nil) (min ?min2&:(<= ?min2 ?com1)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?gy2))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (<= ?com2 ?gx))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (min-net ?hnn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw86&:(> ?qw86 ?com1))))
  (not (vertical (net-name nil) (min ?qw75&:(<= ?qw75 ?com1)) (max ?qw87&:(> ?qw87 ?com1)) (com ?qz30&:(and (> ?qz30 ?com2) (<= ?qz30 ?max1))) (layer ?lay)))
  (not (horizontal (net-name nil) (max ?min1) (com ?com1)))
  (not (vertical (net-name nil) (min ?qw76&:(<= ?qw76 ?com1)) (max ?qw88&:(> ?qw88 ?com1)) (com ?com2) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-y ?com1)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (max ?min1) (com ?com1) (layer ?lay)))
  =>
  (assert (vertical (min ?min2) (max ?com1) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (min-net ?hnn) (max-net ?nn)))
  (modify ?f4 (min =(+ ?com1 1)) (min-net ?nn))
  (assert (vertical (min ?com1) (max =(+ ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-y =(+ ?com1 1)) (grid-x ?com2) (grid-layer ?lay) (came-from south)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p237
  ?f1 <- (context (present loose-constraint))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy2&:(< ?gy2 ?com1)))
  ?f4 <- (vertical (net-name nil) (min ?min2&:(<= ?min2 ?gy2)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?com1))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (>= ?com2 ?gx))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (min-net ?hnn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?com1)) (max ?qw63&:(>= ?qw63 ?com1))))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?qz47&:(and (< ?qz47 ?com2) (>= ?qz47 ?min1))) (layer ?lay)))
  (not (horizontal (net-name nil) (max ?min1) (com ?com1)))
  (not (vertical (net-name nil) (min ?qz22&:(< ?qz22 ?com1)) (max ?qw64&:(>= ?qw64 ?com1)) (com ?com2) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-y ?com1)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (max ?min1) (com ?com1) (layer ?lay)))
  =>
  (assert (vertical (min ?com1) (max ?max2) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (min-net ?nn)))
  (modify ?f4 (max =(- ?com1 1)) (max-net ?nn))
  (assert (vertical (max ?com1) (min =(- ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-y =(- ?com1 1)) (grid-x ?com2) (grid-layer ?lay) (came-from north)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p238
  ?f1 <- (context (present loose-constraint))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy2&:(< ?gy2 ?com1)))
  ?f4 <- (vertical (net-name nil) (min ?min2&:(<= ?min2 ?gy2)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?com1))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (<= ?com2 ?gx))) (layer ?lay) (commo ?cmo2) (compo ?cpo2) (min-net ?hnn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?com1)) (max ?qw64&:(>= ?qw64 ?com1))))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?qz30&:(and (> ?qz30 ?com2) (<= ?qz30 ?max1))) (layer ?lay)))
  (not (horizontal (net-name nil) (max ?min1) (com ?com1)))
  (not (vertical (net-name nil) (min ?qz22&:(< ?qz22 ?com1)) (max ?qw65&:(>= ?qw65 ?com1)) (com ?com2) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-y ?com1)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (max ?min1) (com ?com1) (layer ?lay)))
  =>
  (assert (vertical (min ?com1) (max ?max2) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (min-net ?nn)))
  (modify ?f4 (max =(- ?com1 1)) (max-net ?nn))
  (assert (vertical (max ?com1) (min =(- ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-y =(- ?com1 1)) (grid-x ?com2) (grid-layer ?lay) (came-from north)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p239
  ?f1 <- (context (present loose-constraint0))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from north))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side top))
  (vertical (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?vcom&:(> ?vcom ?gx)) (layer ?garb1) (compo ?garb2) (commo ?garb3) (pin-name ~?pn))
  (not (ff (net-name ?nn) (grid-y ?qw86&:(> ?qw86 ?gy))))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw63&:(>= ?qw63 ?vcom)) (com ?gy)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw64&:(>= ?qw64 ?vcom)) (com ?gy)))
  ?f5 <- (vertical (net-name nil) (min ?min&:(< ?min ?gy)) (max ?qw65&:(>= ?qw65 ?gy)) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?mn))
  (not (horizontal (net-name ~nil&~?nn) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (layer ?lay) (compo ?gy)))
  (congestion (direction row) (coordinate ?gy) (como ?hcmo))
  (not (vertical (status nil) (net-name ~nil&~?nn) (min ?qw74&:(<= ?qw74 ?hcmo)) (max ?qw66&:(>= ?qw66 ?hcmo)) (com ?gx) (layer ?lay)))
  =>
  (assert (vertical (net-name nil) (max ?hcmo) (min ?min) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (min-net ?mn) (max-net ?nn)))
  (modify ?f5 (min ?gy) (min-net ?nn))
  (assert (vertical (net-name ?nn) (max ?gy) (min ?hcmo) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (pin-name ?pn)))
  (modify ?f2 (grid-y ?hcmo) (can-chng-layer nil))
  (modify ?f1 (present propagate-constraint))
)

(defrule p240
  ?f1 <- (context (present loose-constraint0))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from north))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side top))
  (vertical (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?vcom&:(< ?vcom ?gx)) (layer ?garb1) (compo ?garb2) (commo ?garb3) (pin-name ~?pn))
  (not (ff (net-name ?nn) (grid-y ?qw86&:(> ?qw86 ?gy))))
  (not (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?gy)))
  ?f5 <- (vertical (net-name nil) (min ?min&:(< ?min ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?mn))
  (not (horizontal (net-name ~nil&~?nn) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw46&:(>= ?qw46 ?gx)) (layer ?lay) (compo ?gy)))
  (congestion (direction row) (coordinate ?gy) (como ?hcmo))
  (not (vertical (status nil) (net-name ~nil&~?nn) (min ?qw76&:(<= ?qw76 ?hcmo)) (max ?qw64&:(>= ?qw64 ?hcmo)) (com ?gx) (layer ?lay)))
  =>
  (assert (vertical (net-name nil) (max ?hcmo) (min ?min) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (min-net ?mn) (max-net ?nn)))
  (modify ?f5 (min ?gy) (min-net ?nn))
  (assert (vertical (net-name ?nn) (max ?gy) (min ?hcmo) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (pin-name ?pn)))
  (modify ?f2 (grid-y ?hcmo) (can-chng-layer nil))
  (modify ?f1 (present propagate-constraint))
)

(defrule p241
  ?f1 <- (context (present loose-constraint0))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from south))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side bottom))
  (vertical (net-name ?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw86&:(> ?qw86 ?gy)) (com ?vcom&:(> ?vcom ?gx)) (layer ?garb1) (compo ?garb2) (commo ?garb3) (pin-name ~?pn))
  (not (ff (net-name ?nn) (grid-y ?qz20&:(< ?qz20 ?gy))))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?gy)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw63&:(>= ?qw63 ?vcom)) (com ?gy)))
  ?f5 <- (vertical (net-name nil) (min ?qw40&:(<= ?qw40 ?gy)) (max ?max&:(> ?max ?gy)) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (max-net ?mn))
  (not (horizontal (net-name ~nil&~?nn) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (layer ?lay) (compo ?gy)))
  (congestion (direction row) (coordinate ?hcmo) (como ?gy))
  (not (vertical (status nil) (net-name ~nil&~?nn) (min ?qw74&:(<= ?qw74 ?hcmo)) (max ?qw64&:(>= ?qw64 ?hcmo)) (com ?gx) (layer ?lay)))
  =>
  (assert (vertical (net-name nil) (max ?max) (min ?hcmo) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (max-net ?mn) (min-net ?nn)))
  (modify ?f5 (max ?gy) (max-net ?nn))
  (assert (vertical (net-name ?nn) (min ?gy) (max ?hcmo) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (pin-name ?pn)))
  (modify ?f2 (grid-y ?hcmo) (can-chng-layer nil))
  (modify ?f1 (present propagate-constraint))
)

(defrule p242
  ?f1 <- (context (present loose-constraint0))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from south))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side bottom))
  (vertical (net-name ?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw86&:(> ?qw86 ?gy)) (com ?vcom&:(< ?vcom ?gx)) (layer ?garb1) (compo ?garb2) (commo ?garb3) (pin-name ~?pn))
  (not (ff (net-name ?nn) (grid-y ?qz20&:(< ?qz20 ?gy))))
  (not (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?gy)))
  ?f5 <- (vertical (net-name nil) (min ?qw40&:(<= ?qw40 ?gy)) (max ?max&:(> ?max ?gy)) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (max-net ?mn))
  (not (horizontal (net-name ~nil&~?nn) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw46&:(>= ?qw46 ?gx)) (layer ?lay) (compo ?gy)))
  (congestion (direction row) (coordinate ?hcmo) (como ?gy))
  (not (vertical (status nil) (net-name ~nil&~?nn) (min ?qw76&:(<= ?qw76 ?hcmo)) (max ?qw62&:(>= ?qw62 ?hcmo)) (com ?gx) (layer ?lay)))
  =>
  (assert (vertical (net-name nil) (max ?max) (min ?hcmo) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (max-net ?mn) (min-net ?nn)))
  (modify ?f5 (max ?gy) (max-net ?nn))
  (assert (vertical (net-name ?nn) (min ?gy) (max ?hcmo) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (pin-name ?pn)))
  (modify ?f2 (grid-y ?hcmo) (can-chng-layer nil))
  (modify ?f1 (present propagate-constraint))
)

(defrule p243
  ?f1 <- (context (present loose-constraint0))
  =>
  (modify ?f1 (present loose-constraint))
)

(defrule p363
  ?f1 <- (move-via ?nn $?)
  =>
  (retract ?f1)
  (assert (finally-routed ?nn))
)

(defrule p364
  ?f1 <- (move-via ?nn $?)
  (horizontal (status nil) (net-name ?nn&~nil) (min ?hmin) (max ?hmax) (com ?hcom) (layer ?hlay) (compo ?garb1) (commo ?hcmo) (pin-name ?pn))
  ?f3 <- (vertical (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?hcmo)) (max ?hcom) (com ?vcom&:(and (>= ?vcom ?hmin) (<= ?vcom ?hmax))) (layer ?vlay&~?hlay))
  ?f4 <- (vertical (net-name nil) (min ?vmin2&:(<= ?vmin2 ?hcmo)) (max ?vmax2&:(and (> ?vmax2 ?vmin2) (>= ?vmax2 ?hcom))) (com ?vcom) (layer ?hlay) (compo ?vcpo) (commo ?vcmo) (min-net ?mn))
  (not (horizontal (net-name ~?nn) (min ?qz20&:(< ?qz20 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?hcmo)))
  (not (vertical (net-name ~?nn) (min ?qz21&:(< ?qz21 ?hcmo)) (max ?hcmo) (com ?vcom)))
  =>
  (modify ?f3 (max ?hcmo))
  (assert (vertical (layer ?vlay) (max ?hcom) (min ?hcmo) (com ?vcom) (min-net ?nn) (commo ?vcmo) (compo ?vcpo)))
  (assert (vertical (layer ?hlay) (min ?vmin2) (max ?hcmo) (commo ?vcmo) (compo ?vcpo) (min-net ?mn) (max-net ?nn) (com ?vcom)))
  (modify ?f4 (min ?hcom) (min-net ?nn))
  (assert (vertical (net-name ?nn) (pin-name ?pn) (layer ?hlay) (min ?hcmo) (max ?hcom) (commo ?vcmo) (compo ?vcpo) (com ?vcom)))
  (retract ?f1)
  (assert (move-via ?nn ?vlay ?vcom ?hcom))
)

(defrule p365
  ?f1 <- (move-via ?nn $?)
  (horizontal (status nil) (net-name ?nn&~nil) (min ?hmin) (max ?hmax) (com ?hcom) (layer ?hlay) (compo ?garb1) (commo ?hcmo) (pin-name ?pn))
  ?f3 <- (vertical (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?hcmo)) (max ?hcom) (com ?vcom&:(and (>= ?vcom ?hmin) (<= ?vcom ?hmax))) (layer ?vlay&~?hlay))
  ?f4 <- (vertical (net-name nil) (min ?vmin2&:(<= ?vmin2 ?hcmo)) (max ?vmax2&:(and (> ?vmax2 ?vmin2) (>= ?vmax2 ?hcom))) (com ?vcom) (layer ?hlay) (compo ?vcpo) (commo ?vcmo) (min-net ?mn))
  (not (horizontal (net-name ~?nn) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw86&:(> ?qw86 ?vcom)) (com ?hcmo)))
  (not (vertical (net-name ~?nn) (min ?qz20&:(< ?qz20 ?hcmo)) (max ?hcmo) (com ?vcom)))
  =>
  (modify ?f3 (max ?hcmo))
  (assert (vertical (layer ?vlay) (max ?hcom) (min ?hcmo) (com ?vcom) (min-net ?nn) (commo ?vcmo) (compo ?vcpo)))
  (assert (vertical (layer ?hlay) (min ?vmin2) (max ?hcmo) (commo ?vcmo) (compo ?vcpo) (min-net ?mn) (max-net ?nn) (com ?vcom)))
  (modify ?f4 (min ?hcom) (min-net ?nn))
  (assert (vertical (net-name ?nn) (pin-name ?pn) (layer ?hlay) (min ?hcmo) (max ?hcom) (commo ?vcmo) (compo ?vcpo) (com ?vcom)))
  (retract ?f1)
  (assert (move-via ?nn ?vlay ?vcom ?hcom))
)

(defrule p366
  ?f1 <- (move-via ?nn $?)
  (vertical (status nil) (net-name ?nn&~nil) (min ?vmin) (max ?vmax) (com ?vcom) (layer ?vlay) (compo ?garb1) (commo ?vcmo) (pin-name ?pn))
  ?f3 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?vcmo)) (max ?vcom) (com ?hcom&:(and (>= ?hcom ?vmin) (<= ?hcom ?vmax))) (layer ?hlay&~?vlay))
  ?f4 <- (horizontal (net-name nil) (min ?hmin2&:(<= ?hmin2 ?vcmo)) (max ?hmax2&:(and (> ?hmax2 ?hmin2) (>= ?hmax2 ?vcom))) (com ?hcom) (layer ?vlay) (compo ?hcpo) (commo ?hcmo) (min-net ?mn))
  (not (vertical (net-name ~?nn) (min ?qz20&:(< ?qz20 ?hcom)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?vcmo)))
  (not (horizontal (net-name ~?nn) (min ?qz21&:(< ?qz21 ?vcmo)) (max ?vcmo) (com ?hcom)))
  =>
  (modify ?f3 (max ?vcmo))
  (assert (horizontal (layer ?hlay) (max ?vcom) (min ?vcmo) (com ?hcom) (min-net ?nn) (commo ?hcmo) (compo ?hcpo)))
  (assert (horizontal (layer ?vlay) (min ?hmin2) (max ?vcmo) (commo ?hcmo) (compo ?hcpo) (min-net ?mn) (max-net ?nn) (com ?hcom)))
  (modify ?f4 (min ?vcom) (min-net ?nn))
  ;(assert (horizontal (net-name ?nn) (pin-name ?pn) (layer ?vlay) (min ?vcmo) (max ?vcom) (commo ?hcmo) (compo ?hcpo) (com ?hcom)))
  ; this error originates with the original OPS5 program
  (assert (horizotnal ?nn ?pn ?vlay ?vcmo ?vcom ?hcmo ?hcpo ?hcom))
  (retract ?f1)
  (assert (move-via ?nn ?hlay ?hcom ?vcom))
)

(defrule p367
  ?f1 <- (move-via ?nn $?)
  (vertical (status nil) (net-name ?nn&~nil) (min ?vmin) (max ?vmax) (com ?vcom) (layer ?vlay) (compo ?garb1) (commo ?vcmo) (pin-name ?pn))
  ?f3 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?qw75&:(<= ?qw75 ?vcmo)) (com ?hcom&:(and (>= ?hcom ?vmin) (<= ?hcom ?vmax))) (max ?vcom) (layer ?hlay&~?vlay))
  ?f4 <- (horizontal (net-name nil) (min ?hmin2&:(<= ?hmin2 ?vcmo)) (max ?hmax2&:(and (> ?hmax2 ?hmin2) (>= ?hmax2 ?vcom))) (com ?hcom) (layer ?vlay) (compo ?hcpo) (commo ?hcmo) (min-net ?mn))
  (not (vertical (net-name ~?nn) (min ?qw76&:(<= ?qw76 ?hcom)) (max ?qw86&:(> ?qw86 ?hcom)) (com ?vcmo)))
  (not (horizontal (net-name ~?nn) (min ?qz20&:(< ?qz20 ?vcmo)) (max ?vcmo) (com ?hcom)))
  =>
  (modify ?f3 (max ?vcmo))
  (assert (horizontal (layer ?hlay) (max ?vcom) (min ?vcmo) (com ?hcom) (min-net ?nn) (commo ?hcmo) (compo ?hcpo)))
  (assert (horizontal (layer ?vlay) (min ?hmin2) (max ?vcmo) (commo ?hcmo) (compo ?hcpo) (min-net ?mn) (max-net ?nn) (com ?hcom)))
  (modify ?f4 (min ?vcom) (min-net ?nn))
  ;(assert (horizontal (net-name ?nn) (pin-name ?pn) (layer ?vlay) (min ?vcmo) (max ?vcom) (commo ?hcmo) (compo ?hcpo) (com ?hcom)))
  ; this error originates with the original OPS5 program
  (assert (horizotnal ?nn ?pn ?vlay ?vcmo ?vcom ?hcmo ?hcpo ?hcom))
  (retract ?f1)
  (assert (move-via ?nn ?hlay ?hcom ?vcom))
)

(defrule p368
  ?f1 <- (move-via ?nn $?)
  (horizontal (status nil) (net-name ?nn&~nil) (min ?hmin) (max ?hmax) (com ?hcom) (layer ?hlay) (compo ?hcpo) (commo ?garb1) (pin-name ?pn))
  ?f3 <- (vertical (status nil) (net-name ?nn&~nil) (min ?hcom) (max ?qw62&:(>= ?qw62 ?hcpo)) (com ?vcom&:(and (>= ?vcom ?hmin) (<= ?vcom ?hmax))) (layer ?vlay&~?hlay))
  ?f4 <- (vertical (net-name nil) (min ?vmin2&:(<= ?vmin2 ?hcom)) (max ?vmax2&:(and (> ?vmax2 ?vmin2) (>= ?vmax2 ?hcpo))) (com ?vcom) (layer ?hlay) (commo ?vcmo) (compo ?vcpo) (min-net ?mn))
  (not (horizontal (net-name ~?nn) (min ?qz20&:(< ?qz20 ?vcom)) (max ?qw63&:(>= ?qw63 ?vcom)) (com ?hcpo)))
  (not (vertical (net-name ~?nn) (min ?hcpo) (max ?qw87&:(> ?qw87 ?hcpo)) (com ?vcom)))
  =>
  (modify ?f3 (min ?hcpo))
  (assert (vertical (layer ?vlay) (min ?hcom) (max ?hcpo) (com ?vcom) (max-net ?nn) (commo ?vcmo) (compo ?vcpo)))
  (assert (vertical (layer ?hlay) (min ?vmin2) (max ?hcom) (commo ?vcmo) (compo ?vcpo) (min-net ?mn) (max-net ?nn) (com ?vcom)))
  (modify ?f4 (min ?hcpo) (min-net ?nn))
  (assert (vertical (net-name ?nn) (pin-name ?pn) (layer ?hlay) (min ?hcom) (max ?hcpo) (commo ?vcmo) (compo ?vcpo) (com ?vcom)))
  (retract ?f1)
  (assert (move-via ?nn ?vlay ?vcom ?hcom))
)

(defrule p369
  ?f1 <- (move-via ?nn $?)
  (horizontal (status nil) (net-name ?nn&~nil) (min ?hmin) (max ?hmax) (com ?hcom) (layer ?hlay) (compo ?hcpo) (commo ?garb1) (pin-name ?pn))
  ?f3 <- (vertical (status nil) (net-name ?nn&~nil) (min ?hcom) (max ?qw62&:(>= ?qw62 ?hcpo)) (com ?vcom&:(and (>= ?vcom ?hmin) (<= ?vcom ?hmax))) (layer ?vlay&~?hlay))
  ?f4 <- (vertical (net-name nil) (min ?vmin2&:(<= ?vmin2 ?hcom)) (max ?vmax2&:(and (> ?vmax2 ?vmin2) (>= ?vmax2 ?hcpo))) (com ?vcom) (layer ?hlay) (commo ?vcmo) (compo ?vcpo) (min-net ?mn))
  (not (horizontal (net-name ~?nn) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw87&:(> ?qw87 ?vcom)) (com ?hcpo)))
  (not (vertical (net-name ~?nn) (min ?hcpo) (max ?qw88&:(> ?qw88 ?hcpo)) (com ?vcom)))
  =>
  (modify ?f3 (min ?hcpo))
  (assert (vertical (layer ?vlay) (min ?hcom) (max ?hcpo) (com ?vcom) (max-net ?nn) (commo ?vcmo) (compo ?vcpo)))
  (assert (vertical (layer ?hlay) (min ?vmin2) (max ?hcom) (commo ?vcmo) (compo ?vcpo) (min-net ?mn) (max-net ?nn) (com ?vcom)))
  (modify ?f4 (min ?hcpo) (min-net ?nn))
  (assert (vertical (net-name ?nn) (pin-name ?pn) (layer ?hlay) (min ?hcom) (max ?hcpo) (commo ?vcmo) (compo ?vcpo) (com ?vcom)))
  (retract ?f1)
  (assert (move-via ?nn ?vlay ?vcom ?hcom))
)

(defrule p370
  ?f1 <- (move-via ?nn $?)
  (vertical (status nil) (net-name ?nn&~nil) (min ?vmin) (max ?vmax) (com ?vcom) (layer ?vlay) (compo ?vcpo) (commo ?garb1) (pin-name ?pn))
  ?f3 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?vcom) (max ?qw62&:(>= ?qw62 ?vcpo)) (com ?hcom&:(and (>= ?hcom ?vmin) (<= ?hcom ?vmax))) (layer ?hlay&~?vlay))
  ?f4 <- (horizontal (net-name nil) (min ?hmin2&:(<= ?hmin2 ?vcom)) (max ?hmax2&:(and (> ?hmax2 ?hmin2) (>= ?hmax2 ?vcpo))) (com ?hcom) (layer ?vlay) (compo ?hcpo) (commo ?hcmo) (min-net ?mn))
  (not (vertical (net-name ~?nn) (min ?qz20&:(< ?qz20 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?vcpo)))
  (not (horizontal (net-name ~?nn) (min ?vcpo) (max ?qw86&:(> ?qw86 ?vcpo)) (com ?hcom)))
  =>
  (modify ?f3 (min ?vcpo))
  (assert (horizontal (layer ?hlay) (max ?vcpo) (min ?vcom) (com ?hcom) (max-net ?nn) (commo ?hcmo) (compo ?hcpo)))
  (assert (horizontal (layer ?vlay) (min ?hmin2) (max ?vcom) (commo ?hcmo) (compo ?hcpo) (min-net ?mn) (max-net ?nn) (com ?hcom)))
  (modify ?f4 (min ?vcpo) (min-net ?nn))
  ;(assert (horizontal (net-name ?nn) (pin-name ?pn) (layer ?vlay) (min ?vcom) (max ?vcpo) (commo ?hcmo) (compo ?hcpo) (com ?hcom)))
  ; this error originates the original OPS5 program
  (assert (horizotnal ?nn ?pn ?vlay ?vcom ?vcpo ?hcmo ?hcpo ?hcom))
  (retract ?f1)
  (assert (move-via ?nn ?hlay ?hcom ?vcom))
)

(defrule p371
  ?f1 <- (move-via ?nn $?)
  (vertical (status nil) (net-name ?nn&~nil) (min ?vmin) (max ?vmax) (com ?vcom) (layer ?vlay) (compo ?vcpo) (commo ?garb1) (pin-name ?pn))
  ?f3 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?vcom) (max ?qw62&:(>= ?qw62 ?vcpo)) (com ?hcom&:(and (>= ?hcom ?vmin) (<= ?hcom ?vmax))) (layer ?hlay&~?vlay))
  ?f4 <- (horizontal (net-name nil) (min ?hmin2&:(<= ?hmin2 ?vcom)) (max ?hmax2&:(and (> ?hmax2 ?hmin2) (>= ?hmax2 ?vcpo))) (com ?hcom) (layer ?vlay) (compo ?hcpo) (commo ?hcmo) (min-net ?mn))
  (not (vertical (net-name ~?nn) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw86&:(> ?qw86 ?hcom)) (com ?vcmo)))
  (not (horizontal (net-name ~?nn) (min ?vcpo) (max ?qw87&:(> ?qw87 ?vcpo)) (com ?hcom)))
  =>
  (modify ?f3 (min ?vcpo))
  (assert (horizontal (layer ?hlay) (max ?vcpo) (min ?vcom) (com ?hcom) (max-net ?nn) (commo ?hcmo) (compo ?hcpo)))
  (assert (horizontal (layer ?vlay) (min ?hmin2) (max ?vcom) (commo ?hcmo) (compo ?hcpo) (min-net ?mn) (max-net ?nn) (com ?hcom)))
  (modify ?f4 (min ?vcpo) (min-net ?nn))
  ;(assert (horizontal (net-name ?nn) (pin-name ?pn) (layer ?vlay) (min ?vcom) (max ?vcpo) (commo ?hcmo) (compo ?hcpo) (com ?hcom)))
  ; this error originates with the original OPS5 program
  (assert (horizotnal ?nn ?pn ?vlay ?vcom ?vcpo ?hcmo ?hcpo ?hcom))
  (retract ?f1)
  (assert (move-via ?nn ?hlay ?hcom ?vcom))
)

(defrule p372
  (move-via ?nn $?)
  (vertical (status nil) (net-name ?nn&~nil) (min ?vmin1) (max ?vmax1) (com ?vcom1) (layer ?vlay) (compo ?vcpo1) (commo ?vcmo1) (pin-name ?pn1))
  (vertical (status nil) (net-name ?nn&~nil) (min ?vmax1) (max ?vmax2) (com ?vcom2&:(> ?vcom2 ?vcom1)) (layer ?vlay) (compo ?vcpo2) (commo ?vcmo2) (pin-name ?pn2))
  ?h1 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?vcom1) (max ?vcom2) 
                     (com ?vmax1) (layer ?hlay&~?vlay)
                     (max-net ?qt6) (compo ?qt7) (commo ?qt8) (min-net ?qt9) (pin-name ?qt10))
  ?h2 <- (horizontal (status nil) (net-name nil) (min ?fmin&:(<= ?fmin ?vcom1)) 
                     (max ?fmax&:(>= ?fmax ?vcom2)) (com ?vmax1) (layer ?vlay)
                     (compo ?qt1) (commo ?qt2) (min-net ?qt3) (pin-name ?qt4) (max-net ?qt5))
  (not (vertical (min ?qz20&:(< ?qz20 ?vmax1)) (max ?qw62&:(>= ?qw62 ?vmax1)) (com ?qz38&:(and (> ?qz38 ?vcom1) (< ?qz38 ?vcom2)))))
  =>
  (retract ?h2)
  (modify ?h1 (net-name nil) (pin-name nil) (min-net nil) (max-net nil))
  (assert (horizontal (min ?fmin) (max ?vcmo1) (com ?vmax1) (max-net nil)
                      (layer ?vlay) (status nil) (net-name nil)
                      (compo ?qt1) (commo ?qt2) (min-net ?qt3) (pin-name ?qt4)))
  (assert (horizontal (min ?vcpo2) (max ?fmax) (com ?vmax1) (min-net nil)
                      (layer ?vlay) (status nil) (net-name nil)
                      (compo ?qt1) (commo ?qt2) (max-net ?qt5) (pin-name ?qt4)))
  (assert (horizontal (min ?vcom1) (max ?vcom2) (com ?vmax1) 
                      (layer ?vlay) (net-name ?nn) (status nil) 
                      (max-net ?qt6) (compo ?qt7) (commo ?qt8) (min-net ?qt9) (pin-name ?qt10))))


   
(defrule p373
  (move-via ?nn ?lay ?x ?y)
  ?f2 <- (horizontal (min ?x) (max ?garb1) (com ?y) (layer ?lay) 
                   (min-net ?nn) (max-net ?garb2))
  =>
  (modify ?f2 (min-net nil)))

(defrule p374
  (move-via ?nn ?lay ?x ?y)
  ?f2 <- (vertical (min ?y) (max ?garb1) (com ?x) (layer ?lay) 
                 (min-net ?nn) (max-net ?garb2))
  =>
  (modify ?f2 (min-net nil)))

(defrule p375
  (move-via ?nn ?lay ?x ?y)
  ?f2 <- (horizontal (min ?garb1) (max ?x) (com ?y) (layer ?lay) 
                   (min-net ?garb2) (max-net ?nn))
  =>
  (modify ?f2 (max-net nil)))

(defrule p376
  (move-via ?nn ?lay ?x ?y)
  ?f2 <- (vertical (min ?garb1) (max ?y) (com ?x) (layer ?lay) 
                 (min-net ?garb2) (max-net ?nn))
  =>
  (modify ?f2 (max-net nil)))




