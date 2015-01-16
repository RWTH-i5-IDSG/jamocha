


(defrule p55
  (context (present propagate-constraint))
  (horizontal (net-name ?nn1&~nil) (min ?ppgarb) (max ?gx) (com ?gy))
  (horizontal (net-name ~nil&~?nn1) (min ?hmin1&:(> ?hmin1 ?gx)) (max ?garb1) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw86&:(> ?qw86 ?gx)) (com ?gy) (layer ~?lay)))
  (horizontal (net-name ~nil&~?nn1) (min ?qw74&:(<= ?qw74 ?hmin1)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?cmo) (layer ?lay))
  (not (vertical (status nil) (net-name ?nn1&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?qz38&:(and (> ?qz38 ?gx) (< ?qz38 ?hmin1))) (layer ~?lay)))
  (not (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?gy)) (max ?qw87&:(> ?qw87 ?gy)) (com ?qz39&:(and (> ?qz39 ?gx) (< ?qz39 ?hmin1))) (layer ~?lay)))
  (not (vertical (net-name nil) (min ?gy) (max ?qw88&:(> ?qw88 ?gy)) (com ?qz40&:(and (> ?qz40 ?gx) (< ?qz40 ?hmin1))) (layer ~?lay) (min-net nil)))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?gy)) (max ?gy) (com ?qz41&:(and (> ?qz41 ?gx) (< ?qz41 ?hmin1))) (layer ~?lay) (max-net nil)))
  ?h1 <- (horizontal (net-name nil) (min ?gx) (max ?qz30&:(and (> ?qz30 ?gx) (<= ?qz30 ?hmin1))) (com ?gy) (layer ?lay))
  (vertical (com ?hmin2) (compo ?hmin1) (commo ?garb10))
  (vertical (com ?hmax2) (compo ?garb11) (commo ?gx))
  (horizontal (net-name ~nil&~?nn1) (min ?qw75&:(<= ?qw75 ?hmax2)) (max ?qw63&:(>= ?qw63 ?hmin2)) (com ?cmo) (layer ?lay))
  (last-row ?gy)
  =>
  (modify ?h1 (min ?hmax2) (min-net nil))
)

(defrule p56
  (context (present propagate-constraint))
  (horizontal (net-name ?nn1&~nil) (min ?gx) (max ?ppgarb) (com ?gy))
  (horizontal (net-name ~nil&~?nn1) (min ?garb1) (max ?hmax1&:(< ?hmax1 ?gx)) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ~?lay)))
  (horizontal (net-name ~nil&~?nn1) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw63&:(>= ?qw63 ?hmax1)) (com ?cpo) (layer ?lay))
  (not (vertical (status nil) (net-name ?nn1&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?qz38&:(and (> ?qz38 ?hmax1) (< ?qz38 ?gx))) (layer ~?lay)))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?gy)) (max ?qw86&:(> ?qw86 ?gy)) (com ?qz39&:(and (> ?qz39 ?hmax1) (< ?qz39 ?gx))) (layer ~?lay)))
  (not (vertical (net-name nil) (min ?gy) (max ?qw87&:(> ?qw87 ?gy)) (com ?qz40&:(and (> ?qz40 ?hmax1) (< ?qz40 ?gx))) (layer ~?lay) (min-net nil)))
  (not (vertical (net-name nil) (min ?qz22&:(< ?qz22 ?gy)) (max ?gy) (com ?qz41&:(and (> ?qz41 ?hmax1) (< ?qz41 ?gx))) (layer ~?lay) (max-net nil)))
  ?h1 <- (horizontal (net-name nil) (min ?qz47&:(and (< ?qz47 ?gx) (>= ?qz47 ?hmax1))) (max ?gx) (com ?gy) (layer ?lay) (commo 0))
  (vertical (com ?hmin2) (compo ?garb10) (commo ?hmax1))
  (vertical (com ?hmax2) (compo ?gx) (commo ?garb11))
  (horizontal (net-name ~nil&~?nn1) (min ?qw74&:(<= ?qw74 ?hmin2)) (max ?qw64&:(>= ?qw64 ?hmax2)) (com ?cpo) (layer ?lay))
  =>
  (modify ?h1 (max ?hmax2) (max-net nil))
)

(defrule p57
  (context (present propagate-constraint))
  (horizontal (net-name ?nn1&~nil) (min ?ppgarb) (max ?gx) (com ?gy))
  (horizontal (net-name ~nil&~?nn1) (min ?hmin1&:(> ?hmin1 ?gx)) (max ?garb1) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw86&:(> ?qw86 ?gx)) (com ?gy) (layer ~?lay)))
  (horizontal (net-name ~nil&~?nn1) (min ?qw74&:(<= ?qw74 ?hmin1)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?cpo) (layer ?lay))
  (not (vertical (status nil) (net-name ?nn1&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?qz38&:(and (> ?qz38 ?gx) (< ?qz38 ?hmin1))) (layer ~?lay)))
  (not (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?gy)) (max ?qw87&:(> ?qw87 ?gy)) (com ?qz39&:(and (> ?qz39 ?gx) (< ?qz39 ?hmin1))) (layer ~?lay)))
  (not (vertical (net-name nil) (min ?gy) (max ?qw88&:(> ?qw88 ?gy)) (com ?qz40&:(and (> ?qz40 ?gx) (< ?qz40 ?hmin1))) (layer ~?lay) (min-net nil)))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?gy)) (max ?gy) (com ?qz41&:(and (> ?qz41 ?gx) (< ?qz41 ?hmin1))) (layer ~?lay) (max-net nil)))
  ?h1 <- (horizontal (net-name nil) (min ?gx) (max ?qz30&:(and (> ?qz30 ?gx) (<= ?qz30 ?hmin1))) (com ?gy) (layer ?lay) (commo 0))
  (vertical (com ?hmin2) (compo ?hmin1) (commo ?garb10))
  (vertical (com ?hmax2) (compo ?garb11) (commo ?gx))
  (horizontal (net-name ~nil&~?nn1) (min ?qw75&:(<= ?qw75 ?hmax2)) (max ?qw62&:(>= ?qw62 ?hmin2)) (com ?cpo) (layer ?lay))
  =>
  (modify ?h1 (min ?hmax2) (min-net nil))
)

(defrule p58
  (context (present propagate-constraint))
  (vertical (net-name ?nn1&~nil) (min ?gy) (max ?ppgarb) (com ?gx))
  (vertical (net-name ~nil&~?nn1) (min ?garb1) (max ?vmax1&:(< ?vmax1 ?gy)) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx) (layer ~?lay)))
  (vertical (net-name ~nil&~?nn1) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw63&:(>= ?qw63 ?vmax1)) (com ?cpo) (layer ?lay))
  (vertical (net-name ~nil&~?nn1) (min ?qw40&:(<= ?qw40 ?gy)) (max ?qw64&:(>= ?qw64 ?vmax1)) (com ?cmo) (layer ?lay))
  (not (horizontal (status nil) (net-name ?nn1&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz38&:(and (> ?qz38 ?vmax1) (< ?qz38 ?gy))) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw86&:(> ?qw86 ?gx)) (com ?qz39&:(and (> ?qz39 ?vmax1) (< ?qz39 ?gy))) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?gx) (max ?qw87&:(> ?qw87 ?gx)) (com ?qz40&:(and (> ?qz40 ?vmax1) (< ?qz40 ?gy))) (layer ~?lay) (min-net nil)))
  (not (horizontal (net-name nil) (min ?qz22&:(< ?qz22 ?gx)) (max ?gx) (com ?qz41&:(and (> ?qz41 ?vmax1) (< ?qz41 ?gy))) (layer ~?lay) (max-net nil)))
  ?h1 <- (vertical (net-name nil) (min ?qz47&:(and (< ?qz47 ?gy) (>= ?qz47 ?vmax1))) (max ?gy) (com ?gx) (layer ?lay))
  (horizontal (com ?vmin2) (compo ?garb10) (commo ?vmax1))
  (horizontal (com ?vmax2) (compo ?gy) (commo ?garb11))
  (vertical (net-name ~nil&~?nn1) (min ?qw74&:(<= ?qw74 ?vmin2)) (max ?qw65&:(>= ?qw65 ?vmax2)) (com ?cmo) (layer ?lay))
  (vertical (net-name ~nil&~?nn1) (min ?qw75&:(<= ?qw75 ?vmin2)) (max ?qw66&:(>= ?qw66 ?vmax2)) (com ?cpo) (layer ?lay))
  =>
  (modify ?h1 (max ?vmax2) (max-net nil))
)

(defrule p59
  (context (present propagate-constraint))
  (vertical (net-name ?nn1&~nil) (min ?ppgarb) (max ?gy) (com ?gx))
  (vertical (net-name ~nil&~?nn1) (min ?vmin1&:(> ?vmin1 ?gy)) (max ?garb1) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (not (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw86&:(> ?qw86 ?gy)) (com ?gx) (layer ~?lay)))
  (vertical (net-name ~nil&~?nn1) (min ?qw74&:(<= ?qw74 ?vmin1)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?cpo) (layer ?lay))
  (vertical (net-name ~nil&~?nn1) (min ?qw75&:(<= ?qw75 ?vmin1)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?cmo) (layer ?lay))
  (not (horizontal (status nil) (net-name ?nn1&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz38&:(and (> ?qz38 ?gy) (< ?qz38 ?vmin1))) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?gx)) (max ?qw87&:(> ?qw87 ?gx)) (com ?qz39&:(and (> ?qz39 ?gy) (< ?qz39 ?vmin1))) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?gx) (max ?qw88&:(> ?qw88 ?gx)) (com ?qz40&:(and (> ?qz40 ?gy) (< ?qz40 ?vmin1))) (layer ~?lay) (min-net nil)))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?gx) (com ?qz41&:(and (> ?qz41 ?gy) (< ?qz41 ?vmin1))) (layer ~?lay) (max-net nil)))
  ?v1 <- (vertical (net-name nil) (min ?gy) (max ?qz30&:(and (> ?qz30 ?gy) (<= ?qz30 ?vmin1))) (com ?gx) (layer ?lay))
  (horizontal (com ?vmin2) (compo ?vmin1) (commo ?garb10))
  (horizontal (com ?vmax2) (compo ?garb11) (commo ?gy))
  (vertical (net-name ~nil&~?nn1) (min ?qw76&:(<= ?qw76 ?vmax2)) (max ?qw64&:(>= ?qw64 ?vmin2)) (com ?cmo) (layer ?lay))
  (vertical (net-name ~nil&~?nn1) (min ?qw77&:(<= ?qw77 ?vmax2)) (max ?qw65&:(>= ?qw65 ?vmin2)) (com ?cpo) (layer ?lay))
  =>
  (modify ?v1 (min ?vmax2) (min-net nil))
)

(defrule p60
  (context (present propagate-constraint))
  (vertical (net-name ?nn1&~nil) (min ?gy) (max ?ppgarb) (com ?gx))
  (vertical (net-name ~nil&~?nn1) (min ?garb1) (max ?vmax1&:(< ?vmax1 ?gy)) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx) (layer ~?lay)))
  (vertical (net-name ~nil&~?nn1) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw63&:(>= ?qw63 ?vmax1)) (com ?cmo) (layer ?lay))
  (not (horizontal (status nil) (net-name ?nn1&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz38&:(and (> ?qz38 ?vmax1) (< ?qz38 ?gy))) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw86&:(> ?qw86 ?gx)) (com ?qz39&:(and (> ?qz39 ?vmax1) (< ?qz39 ?gy))) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?gx) (max ?qw87&:(> ?qw87 ?gx)) (com ?qz40&:(and (> ?qz40 ?vmax1) (< ?qz40 ?gy))) (layer ~?lay) (min-net nil)))
  (not (horizontal (net-name nil) (min ?qz22&:(< ?qz22 ?gx)) (max ?gx) (com ?qz41&:(and (> ?qz41 ?vmax1) (< ?qz41 ?gy))) (layer ~?lay) (max-net nil)))
  ?v1 <- (vertical (net-name nil) (min ?qz47&:(and (< ?qz47 ?gy) (>= ?qz47 ?vmax1))) (max ?gy) (com ?gx) (layer ?lay))
  (horizontal (com ?vmin2) (compo ?garb10) (commo ?vmax1))
  (horizontal (com ?vmax2) (compo ?gy) (commo ?garb11))
  (vertical (net-name ~nil&~?nn1) (min ?qw74&:(<= ?qw74 ?vmin2)) (max ?qw64&:(>= ?qw64 ?vmax2)) (com ?cmo) (layer ?lay))
  (last-col ?gx)
  =>
  (modify ?v1 (max ?vmax2) (max-net nil))
)

(defrule p61
  (context (present propagate-constraint))
  (vertical (net-name ?nn1&~nil) (min ?ppgarb) (max ?gy) (com ?gx))
  (vertical (net-name ~nil&~?nn1) (min ?vmin1&:(> ?vmin1 ?gy)) (max ?garb1) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (not (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw86&:(> ?qw86 ?gy)) (com ?gx) (layer ~?lay)))
  (vertical (net-name ~nil&~?nn1) (min ?qw74&:(<= ?qw74 ?vmin1)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?cmo) (layer ?lay))
  (not (horizontal (status nil) (net-name ?nn1&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz38&:(and (> ?qz38 ?gy) (< ?qz38 ?vmin1))) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw87&:(> ?qw87 ?gx)) (com ?qz39&:(and (> ?qz39 ?gy) (< ?qz39 ?vmin1))) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?gx) (max ?qw88&:(> ?qw88 ?gx)) (com ?qz40&:(and (> ?qz40 ?gy) (< ?qz40 ?vmin1))) (layer ~?lay) (min-net nil)))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?gx)) (max ?gx) (com ?qz41&:(and (> ?qz41 ?gy) (< ?qz41 ?vmin1))) (layer ~?lay) (max-net nil)))
  ?v1 <- (vertical (net-name nil) (min ?gy) (max ?qz30&:(and (> ?qz30 ?gy) (<= ?qz30 ?vmin1))) (com ?gx) (layer ?lay))
  (horizontal (com ?vmin2) (compo ?vmin1) (commo ?garb10))
  (horizontal (com ?vmax2) (compo ?garb11) (commo ?gy))
  (vertical (net-name ~nil&~?nn1) (min ?qw75&:(<= ?qw75 ?vmax2)) (max ?qw63&:(>= ?qw63 ?vmin2)) (com ?cmo) (layer ?lay))
  (last-col ?gx)
  =>
  (modify ?v1 (min ?vmax2) (min-net nil))
)

(defrule p62
  (context (present propagate-constraint))
  (vertical (net-name ?nn1&~nil) (min ?gy) (max ?ppgarb) (com ?gx))
  (vertical (net-name ~nil&~?nn1) (min ?garb1) (max ?vmax1&:(< ?vmax1 ?gy)) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (not (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx) (layer ~?lay)))
  (vertical (net-name ~nil&~?nn1) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?vmax1)) (com ?cpo) (layer ?lay))
  (not (horizontal (status nil) (net-name ?nn1&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz38&:(and (> ?qz38 ?vmax1) (< ?qz38 ?gy))) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?gx)) (max ?qw86&:(> ?qw86 ?gx)) (com ?qz39&:(and (> ?qz39 ?vmax1) (< ?qz39 ?gy))) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?gx) (max ?qw87&:(> ?qw87 ?gx)) (com ?qz40&:(and (> ?qz40 ?vmax1) (< ?qz40 ?gy))) (layer ~?lay) (min-net nil)))
  (not (horizontal (net-name nil) (min ?qz22&:(< ?qz22 ?gx)) (max ?gx) (com ?qz41&:(and (> ?qz41 ?vmax1) (< ?qz41 ?gy))) (layer ~?lay) (max-net nil)))
  ?v1 <- (vertical (net-name nil) (min ?qz47&:(and (< ?qz47 ?gy) (>= ?qz47 ?vmax1))) (max ?gy) (com ?gx) (layer ?lay) (commo 0))
  (horizontal (com ?vmin2) (compo ?garb10) (commo ?vmax1))
  (horizontal (com ?vmax2) (compo ?gy) (commo ?garb11))
  (vertical (net-name ~nil&~?nn1) (min ?qw74&:(<= ?qw74 ?vmin2)) (max ?qw64&:(>= ?qw64 ?vmax2)) (com ?cpo) (layer ?lay))
  =>
  (modify ?v1 (max ?vmax2) (max-net nil))
)

(defrule p63
  (context (present propagate-constraint))
  (vertical (net-name ?nn1&~nil) (min ?ppgarb) (max ?gy) (com ?gx))
  (vertical (net-name ~nil&~?nn1) (min ?vmin1&:(> ?vmin1 ?gy)) (max ?garb1) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (not (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw87&:(> ?qw87 ?gy)) (com ?gx) (layer ~?lay)))
  (vertical (net-name ~nil&~?nn1) (min ?qw74&:(<= ?qw74 ?vmin1)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?cpo) (layer ?lay))
  (not (horizontal (status nil) (net-name ?nn1&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz38&:(and (> ?qz38 ?gy) (< ?qz38 ?vmin1))) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw86&:(> ?qw86 ?gx)) (com ?qz39&:(and (> ?qz39 ?gy) (< ?qz39 ?vmin1))) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?gx) (max ?qw88&:(> ?qw88 ?gx)) (com ?qz40&:(and (> ?qz40 ?gy) (< ?qz40 ?vmin1))) (layer ~?lay) (min-net nil)))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?gx)) (max ?gx) (com ?qz41&:(and (> ?qz41 ?gy) (< ?qz41 ?vmin1))) (layer ~?lay) (max-net nil)))
  ?v1 <- (vertical (net-name nil) (min ?gy) (max ?qz30&:(and (> ?qz30 ?gy) (<= ?qz30 ?vmin1))) (com ?gx) (layer ?lay) (commo 0))
  (horizontal (com ?vmin2) (compo ?vmin1) (commo ?garb10))
  (horizontal (com ?vmax2) (compo ?garb11) (commo ?gy))
  (vertical (net-name ~nil&~?nn1) (min ?qw75&:(<= ?qw75 ?vmax2)) (max ?qw65&:(>= ?qw65 ?vmin2)) (com ?cpo) (layer ?lay))
  =>
  (modify ?v1 (min ?vmax2) (min-net nil))
)

(defrule p64
  (context (present propagate-constraint))
  ?v <- (vertical (net-name nil) (min ?vmin) (max ?vmax) (com ?vcom) (layer ?lay) (compo ?vcpo) (commo ?vcmo))
  ?h <- (horizontal (net-name nil) (min ?hmin&:(< ?hmin ?vcom)) 
                    (status ?qt1) (min-net ?qt2) (pin-name ?qt3)
                    (max ?hmax&:(> ?hmax ?vcom)) (com ?vmax) (layer ?lay) 
                    (compo ?hcpo) (commo ?hcmo))
  (vertical (net-name ?nn1&~nil) (min ?qz20&:(< ?qz20 ?vmax)) (max ?qw62&:(>= ?qw62 ?vmax)) (com ?vcmo) (layer ?lay) (compo ?vcom) (commo ?garb1))
  (vertical (net-name ?nn2&~?nn1&~nil) (min ?qz21&:(< ?qz21 ?vmax)) (max ?qw63&:(>= ?qw63 ?vmax)) (com ?vcpo) (layer ?lay) (compo ?garb2) (commo ?vcom))
  (horizontal (net-name nil) (min ?qz22&:(< ?qz22 ?vcom)) (max ?qw86&:(> ?qw86 ?vcom)) (com ?hcmo) (layer ?lay))
  (not (vertical (status nil) (min ?vmax) (max ?qw87&:(> ?qw87 ?vmax)) (com ?vcom)))
  (not (horizontal (status nil) (min ?temp&:(<= ?temp ?vcom)) (max ?qz65&:(and (> ?qz65 ?temp) (>= ?qz65 ?vcom))) (com ?vmax) (layer ~?lay)))
  =>
  (modify ?v (max ?hcmo) (max-net nil))
  (assert (horizontal (min ?hmin) (com ?vmax) (compo ?hcpo) (commo ?hcmo) (layer ?lay) 
                      (status ?qt1) (min-net ?qt2) (net-name nil) (pin-name ?qt3)
                      (max ?vcmo) (max-net ?nn1)))
  (modify ?h (min ?vcpo) (min-net ?nn2))
)

(defrule p65
  (context (present propagate-constraint))
  ?v <- (vertical (net-name nil) (min ?vmin) (max ?vmax) (com ?vcom) (layer ?lay) (compo ?vcpo) (commo ?vcmo))
  ?h <- (horizontal (net-name nil) (min ?hmin&:(< ?hmin ?vcom)) 
                    (status ?qt1) (min-net ?qt2) (pin-name ?qt3)
                    (max ?hmax&:(> ?hmax ?vcom)) (com ?vmin) (layer ?lay) 
                    (compo ?hcpo) (commo ?hcmo))
  (vertical (net-name ?nn1&~nil) (min ?qw74&:(<= ?qw74 ?vmin)) (max ?qw86&:(> ?qw86 ?vmin)) (com ?vcmo) (layer ?lay) (compo ?vcom) (commo ?garb1))
  (vertical (net-name ?nn2&~?nn1&~nil) (min ?qw75&:(<= ?qw75 ?vmin)) (max ?qw87&:(> ?qw87 ?vmin)) (com ?vcpo) (layer ?lay) (compo ?garb2) (commo ?vcom))
  (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?vcom)) (max ?qw88&:(> ?qw88 ?vcom)) (com ?hcpo) (layer ?lay))
  (not (vertical (status nil) (min ?qz21&:(< ?qz21 ?vmin)) (max ?vmin) (com ?vcom)))
  (not (horizontal (status nil) (min ?temp&:(<= ?temp ?vcom)) (max ?qz65&:(and (> ?qz65 ?temp) (>= ?qz65 ?vcom))) (com ?vmin) (layer ~?lay)))
  =>
  (modify ?v (min ?hcpo) (min-net nil))
  (assert (horizontal (min ?hmin) (com ?vmin) (compo ?hcpo) (commo ?hcmo) (layer ?lay) 
                      (status ?qt1) (min-net ?qt2) (net-name nil) (pin-name ?qt3)
                      (max ?vcmo) (max-net ?nn1)))
  (modify ?h (min ?vcpo) (min-net ?nn2))
)



(defrule p66
  (context (present propagate-constraint))
  ?h <- (horizontal (net-name nil) (min ?hmin) (max ?hmax) (com ?hcom) 
                    (layer ?lay) (compo ?hcpo) (commo ?hcmo))
  ?v <- (vertical (net-name nil) (min ?vmin&:(< ?vmin ?hcom)) 
                  (status ?qt1) (max-net ?qt2) (pin-name ?qt3)
                  (max ?vmax&:(> ?vmax ?hcom)) (com ?hmax) (layer ?lay) 
                  (compo ?vcpo) (commo ?vcmo))
  (horizontal (net-name ?nn1&~nil) (min ?qz20&:(< ?qz20 ?hmax)) (max ?qw62&:(>= ?qw62 ?hmax)) (com ?hcmo) (layer ?lay) (compo ?hcom) (commo ?garb1))
  (horizontal (net-name ?nn2&~?nn1&~nil) (min ?qz21&:(< ?qz21 ?hmax)) (max ?qw63&:(>= ?qw63 ?hmax)) (com ?hcpo) (layer ?lay) (compo ?garb2) (commo ?hcom))
  (vertical (net-name nil) (min ?qz22&:(< ?qz22 ?hcom)) (max ?qw86&:(> ?qw86 ?hcom)) (com ?vcmo) (layer ?lay))
  (not (horizontal (status nil) (min ?hmax) (max ?qw2&:(> ?qw2 ?hmax)) (com ?hcom)))
  (not (vertical (status nil) (min ?temp&:(<= ?temp ?hcom)) (max ?qz65&:(and (> ?qz65 ?temp) (>= ?qz65 ?hcom))) (com ?hmax) (layer ~?lay)))
  =>
  (modify ?h (max ?vcmo) (max-net nil))
  (assert (vertical (min ?vmin) (com ?hmax) (compo ?vcpo) (commo ?vcmo) (layer ?lay) 
                    (status ?qt1) (min-net ?nn2) (net-name nil) (pin-name ?qt3)
                    (max ?hcmo) (max-net ?qt2)))
  (modify ?v (min ?hcpo) (min-net ?nn2))
)

(defrule p67
  (context (present propagate-constraint))
  ?h <- (horizontal (net-name nil) (min ?hmin) (max ?hmax) (com ?hcom) (layer ?lay) (compo ?hcpo) (commo ?hcmo))
  ?v <- (vertical (net-name nil) (min ?vmin&:(< ?vmin ?hcom))
                  (max ?vmax&:(> ?vmax ?hcom)) (com ?hmin) (layer ?lay) 
                  (status ?qt1) (min-net ?qt2) (pin-name ?qt3)
                  (compo ?vcpo) (commo ?vcmo))
  (horizontal (net-name ?nn1&~nil) (min ?qw74&:(<= ?qw74 ?hmin)) (max ?qw86&:(> ?qw86 ?hmin)) (com ?hcmo) (layer ?lay) (compo ?hcom) (commo ?garb1))
  (horizontal (net-name ?nn2&~?nn1&~nil) (min ?qw75&:(<= ?qw75 ?hmin)) (max ?qw87&:(> ?qw87 ?hmin)) (com ?hcpo) (layer ?lay) (compo ?garb2) (commo ?hcom))
  (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?hcom)) (max ?qw88&:(> ?qw88 ?hcom)) (com ?vcpo) (layer ?lay))
  (not (horizontal (status nil) (min ?qz21&:(< ?qz21 ?hmin)) (max ?hmin) (com ?hcom)))
  (not (vertical (status nil) (min ?temp&:(<= ?temp ?hcom)) (max ?qz65&:(and (> ?qz65 ?temp) (>= ?qz65 ?hcom))) (com ?hmin) (layer ~?lay)))
  =>
  (modify ?h (min ?vcpo) (min-net nil))
  (assert (vertical (min ?vmin) (com ?hmin) (compo ?vcpo) (commo ?vcmo) (layer ?lay) 
                    (status ?qt1) (min-net ?qt2) (net-name nil) (pin-name ?qt3)
                    (max ?hcmo) (max-net ?nn1)))
  (modify ?v (min ?hcpo) (min-net ?nn2))
)

(defrule p68
  (context (present propagate-constraint))
  ?v <- (vertical (net-name nil) (min ?vmin) (max ?vmax) (com ?vcom) (layer ?lay) (compo ?vcpo) (commo ?vcmo))
  ?h <- (horizontal (net-name nil) (min ?hmin&:(< ?hmin ?vcom)) (max ?vcom) (com ?vmax) (layer ?lay) (compo ?hcpo) (commo ?hcmo))
  (vertical (net-name ?nn1&~nil) (min ?qz20&:(< ?qz20 ?vmax)) (max ?qw62&:(>= ?qw62 ?vmax)) (com ?vcmo) (layer ?lay) (compo ?vcom) (commo ?garb1))
  (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?vcom)) (max ?qw64&:(>= ?qw64 ?vcom)) (com ?hcmo) (layer ?lay))
  (not (vertical (net-name nil) (min ?vmax) (max ?qw86&:(> ?qw86 ?vmax)) (com ?vcom) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?vcom) (max ?qw87&:(> ?qw87 ?vcom)) (com ?vmax) (layer ~?lay)))
  =>
  (modify ?h (max ?vcmo) (max-net ?nn1))
)

(defrule p69
  (context (present propagate-constraint))
  ?v <- (vertical (net-name nil) (min ?vmin) (max ?vmax) (com ?vcom) (layer ?lay) (compo ?vcpo) (commo ?vcmo))
  ?h <- (horizontal (net-name nil) (min ?vcom) (max ?hmax&:(> ?hmax ?vcom)) (com ?vmax) (layer ?lay) (compo ?hcpo) (commo ?hcmo))
  (vertical (net-name ?nn1&~nil) (min ?qz20&:(< ?qz20 ?vmax)) (max ?qw62&:(>= ?qw62 ?vmax)) (com ?vcpo) (layer ?lay) (compo ?garb1) (commo ?vcom))
  (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw86&:(> ?qw86 ?vcom)) (com ?hcmo) (layer ?lay))
  (not (vertical (net-name nil) (min ?vmax) (max ?qw88&:(> ?qw88 ?vmax)) (com ?vcom) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?vcom)) (max ?vcom) (com ?vmax) (layer ~?lay)))
  =>
  (modify ?h (min ?vcpo) (min-net ?nn1))
)



(defrule p70
  (context (present propagate-constraint))
  ?v <- (vertical (net-name nil) (min ?vmin) (max ?vmax) (com ?vcom) (layer ?lay) (compo ?vcpo) (commo ?vcmo))
  ?h <- (horizontal (net-name nil) (min ?hmin&:(< ?hmin ?vcom)) (max ?vcom) (com ?vmin) (layer ?lay) (compo ?hcpo) (commo ?hcmo))
  (vertical (net-name ?nn1&~nil) (min ?qw74&:(<= ?qw74 ?vmin)) (max ?qw86&:(> ?qw86 ?vmin)) (com ?vcmo) (layer ?lay) (compo ?vcom) (commo ?garb1))
  (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?hcpo) (layer ?lay))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?vmin)) (max ?vmin) (com ?vcom) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?vcom) (max ?qw87&:(> ?qw87 ?vcom)) (com ?vmin) (layer ~?lay)))
  =>
  (modify ?h (max ?vcmo) (max-net ?nn1))
)

(defrule p71
  (context (present propagate-constraint))
  ?v <- (vertical (net-name nil) (min ?vmin) (max ?vmax) (com ?vcom) (layer ?lay) (compo ?vcpo) (commo ?vcmo))
  ?h <- (horizontal (net-name nil) (min ?vcom) (max ?hmax&:(> ?hmax ?vcom)) (com ?vmin) (layer ?lay) (compo ?hcpo) (commo ?hcmo))
  (vertical (net-name ?nn1&~nil) (min ?qw74&:(<= ?qw74 ?vmin)) (max ?qw86&:(> ?qw86 ?vmin)) (com ?vcpo) (layer ?lay) (compo ?garb1) (commo ?vcom))
  (horizontal (net-name nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw87&:(> ?qw87 ?vcom)) (com ?hcpo) (layer ?lay))
  (not (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?vmin)) (max ?vmin) (com ?vcom) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?vcom)) (max ?vcom) (com ?vmin) (layer ~?lay)))
  =>
  (modify ?h (min ?vcpo) (min-net ?nn1))
)

(defrule p72
  (context (present propagate-constraint))
  ?h <- (horizontal (net-name nil) (min ?hmin) (max ?hmax) (com ?hcom) (layer ?lay) (compo ?hcpo) (commo ?hcmo))
  ?v <- (vertical (net-name nil) (min ?hcom) (max ?vmax&:(> ?vmax ?hcom)) (com ?hmax) (layer ?lay) (compo ?vcpo) (commo ?vcmo))
  (horizontal (net-name ?nn1&~nil) (min ?qz20&:(< ?qz20 ?hmax)) (max ?qw62&:(>= ?qw62 ?hmax)) (com ?hcpo) (layer ?lay) (compo ?garb1) (commo ?hcom))
  (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw86&:(> ?qw86 ?hcom)) (com ?vcmo) (layer ?lay))
  (not (horizontal (net-name nil) (min ?hmax) (max ?qw2&:(> ?qw2 ?hmax)) (com ?hcom) (layer ~?lay)))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?hcom)) (max ?hcom) (com ?hmax) (layer ~?lay)))
  =>
  (modify ?v (min ?hcpo) (min-net ?nn1))
)

(defrule p73
  (context (present propagate-constraint))
  ?h <- (horizontal (net-name nil) (min ?hmin) (max ?hmax) (com ?hcom) (layer ?lay) (compo ?hcpo) (commo ?hcmo))
  ?v <- (vertical (net-name nil) (min ?vmin&:(< ?vmin ?hcom)) (max ?hcom) (com ?hmax) (layer ?lay) (compo ?vcpo) (commo ?vcmo))
  (horizontal (net-name ?nn1&~nil) (min ?qz20&:(< ?qz20 ?hmax)) (max ?qw62&:(>= ?qw62 ?hmax)) (com ?hcmo) (layer ?lay) (compo ?hcom) (commo ?garb1))
  (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?vcmo) (layer ?lay))
  (not (horizontal (net-name nil) (min ?hmax) (max ?qw2&:(> ?qw2 ?hmax)) (com ?hcom) (layer ~?lay)))
  (not (vertical (net-name nil) (min ?hcom) (max ?qw87&:(> ?qw87 ?hcom)) (com ?hmax) (layer ~?lay)))
  =>
  (modify ?v (max ?hcmo) (max-net ?nn1))
)

(defrule p74
  (context (present propagate-constraint))
  ?h <- (horizontal (net-name nil) (min ?hmin) (max ?hmax) (com ?hcom) (layer ?lay) (compo ?hcpo) (commo ?hcmo))
  ?v <- (vertical (net-name nil) (min ?hcom) (max ?vmax&:(> ?vmax ?hcom)) (com ?hmin) (layer ?lay) (compo ?vcpo) (commo ?vcmo))
  (horizontal (net-name ?nn1&~nil) (min ?qw74&:(<= ?qw74 ?hmin)) (max ?qw86&:(> ?qw86 ?hmin)) (com ?hcpo) (layer ?lay) (compo ?garb1) (commo ?hcom))
  (vertical (net-name nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw87&:(> ?qw87 ?hcom)) (com ?vcpo) (layer ?lay))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?hmin)) (max ?hmin) (com ?hcom) (layer ~?lay)))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?hcom)) (max ?hcom) (com ?hmin) (layer ~?lay)))
  =>
  (modify ?v (min ?hcpo) (min-net ?nn1))
)

(defrule p75
  (context (present propagate-constraint))
  ?h <- (horizontal (net-name nil) (min ?hmin) (max ?hmax) (com ?hcom) (layer ?lay) (compo ?hcpo) (commo ?hcmo))
  ?v <- (vertical (net-name nil) (min ?vmin&:(< ?vmin ?hcom)) (max ?hcom) (com ?hmin) (layer ?lay) (compo ?vcpo) (commo ?vcmo))
  (horizontal (net-name ?nn1&~nil) (min ?qw74&:(<= ?qw74 ?hmin)) (max ?qw86&:(> ?qw86 ?hmin)) (com ?hcmo) (layer ?lay) (compo ?hcom) (commo ?garb1))
  (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?hcom)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?vcpo) (layer ?lay))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?hmin)) (max ?hmin) (com ?hcom) (layer ~?lay)))
  (not (vertical (net-name nil) (min ?hcom) (max ?qw87&:(> ?qw87 ?hcom)) (com ?hmin) (layer ~?lay)))
  =>
  (modify ?v (max ?hcmo) (max-net ?nn1))
)

(defrule p76
  (context (present propagate-constraint))
  (vertical (net-name ?nn1&~nil) (min ?vmin) (max ?vmax) (com ?vcom) (layer ?vlay))
  (vertical (net-name nil) (min ?vmax) (max ?qw86&:(> ?qw86 ?vmax)) (com ?vcom) (layer ?vlay))
  (horizontal (net-name ?nn2&~?nn1&~nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?hcom) (layer ?hlay&~?vlay) (compo ?hcpo) (commo ?vmax))
  (not (horizontal (status nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw63&:(>= ?qw63 ?vcom)) (com ?vmax)))
  ?v <- (vertical (net-name nil) (min ?vmax) (max ?qw87&:(> ?qw87 ?vmax)) (com ?vcom) (layer ?hlay))
  =>
  (modify ?v (min ?hcom) (min-net ?nn2))
)

(defrule p77
  (context (present propagate-constraint))
  (vertical (net-name ?nn1&~nil) (min ?vmin) (max ?vmax) (com ?vcom) (layer ?vlay))
  (vertical (net-name nil) (min ?garb1) (max ?vmin&:(> ?vmin ?garb1)) (com ?vcom) (layer ?vlay))
  (horizontal (net-name ?nn2&~?nn1&~nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?hcom) (layer ?hlay&~?vlay) (compo ?vmin) (commo ?hcmo))
  (not (horizontal (status nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw63&:(>= ?qw63 ?vcom)) (com ?vmin)))
  ?v <- (vertical (net-name nil) (min ?garb2) (max ?vmin&:(> ?vmin ?garb2)) (com ?vcom) (layer ?hlay))
  =>
  (modify ?v (max ?hcom) (max-net ?nn2))
)

(defrule p78
  (context (present propagate-constraint))
  (horizontal (net-name ?nn1&~nil) (min ?hmin) (max ?hmax) (com ?hcom) (layer ?hlay))
  (horizontal (net-name nil) (min ?hmax) (max ?qw2&:(> ?qw2 ?hmax)) (com ?hcom) (layer ?hlay))
  (vertical (net-name ?nn2&~?nn1&~nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?vcom) (layer ?vlay&~?hlay) (compo ?vcpo) (commo ?hmax))
  (not (vertical (status nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?hmax)))
  ?h <- (horizontal (net-name nil) (min ?hmax) (max ?qw2&:(> ?qw2 ?hmax)) (com ?hcom) (layer ?vlay))
  =>
  (modify ?h (min ?vcom) (min-net ?nn2))
)

(defrule p79
  (context (present propagate-constraint))
  (horizontal (net-name ?nn1&~nil) (min ?hmin) (max ?hmax) (com ?hcom) (layer ?hlay))
  (horizontal (net-name nil) (min ?garb1) (max ?hmin&:(> ?hmin ?garb1)) (com ?hcom) (layer ?hlay))
  (vertical (net-name ?nn2&~?nn1&~nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?vcom) (layer ?vlay&~?hlay) (compo ?hmin) (commo ?vcmo))
  (not (vertical (status nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?hmin)))
  ?h <- (horizontal (net-name nil) (min ?garb2) (max ?hmin&:(> ?hmin ?garb2)) (com ?hcom) (layer ?vlay))
  =>
  (modify ?h (max ?vcom) (max-net ?nn2))
)

