;;; The Manners Benchmark implemented using CLIPS
;;; The original source for the OPS5 benchmark suite is available
;;; at http://www.cs.utexas.edu/ftp/pub/ops5-benchmark-suite/

;;; Changes made for the CLIPS version
;;;
;;;   Reformatted code
;;;   Changed the logical name to which the program output
;;;     is sent to a global variable so that the program
;;;     output can be easily enabled/disabled.

;;; ##########
;;; Defglobals
;;; ##########

(bind ?*output*  t) ; Disabled = nil Enabled = t

;;; ############
;;; Deftemplates
;;; ############

(deftemplate guest 
   (slot name)
   (slot sex)
   (slot hobby))
   
(deftemplate last_seat 
   (slot seat))
   
(deftemplate seating 
   (slot seat1)
   (slot seat2)
   (slot name1) 
   (slot name2)
   (slot id)
   (slot pid)
   (slot path_done))
   
(deftemplate context
   (slot state))

(deftemplate path 
   (slot id)
   (slot name)
   (slot seat))
   
(deftemplate chosen 
   (slot id)
   (slot name)
   (slot hobby))
   
(deftemplate count 
   (slot c))

;;; ########
;;; Defrules
;;; ########

;;; *****************
;;; assign_first_seat
;;; *****************

(defrule assign_first_seat
   ?f1 <- (context (state start))
   (guest (name ?n))
   ?f3 <- (count (c ?c))
   =>
   (assert (seating (seat1 1) (name1 ?n) (name2 ?n) (seat2 1) (id ?c) (pid 0) (path_done yes)))
   (assert (path (id ?c) (name ?n) (seat 1)))
   (modify ?f3 (c (+ ?c 1)))
   (printout ?*output* "first seat 1 " ?n " " ?n " 1 " ?c " 0 1" crlf)
   (modify ?f1 (state assign_seats)))

;;; ************
;;; find_seating
;;; ************

(defrule find_seating
   ?f1 <- (context (state assign_seats))
   (seating (seat1 ?seat1) (seat2 ?seat2) (name2 ?n2) (id ?id) (pid ?pid) (path_done yes))
   (guest (name ?n2) (sex ?s1) (hobby ?h1))
   (guest (name ?g2) (sex ~?s1) (hobby ?h1))
   ?f5 <- (count (c ?c))
   (not (path (id ?id) (name ?g2)))
   (not (chosen (id ?id) (name ?g2) (hobby ?h1)))
   =>
   (assert (seating (seat1 ?seat2) (name1 ?n2) (name2 ?g2) (seat2 (+ ?seat2 1)) (id ?c) (pid ?id) (path_done no)))
   (assert (path (id ?c) (name ?g2) (seat (+ ?seat2 1))))
   (assert (chosen (id ?id) (name ?g2) (hobby ?h1)))
   (modify ?f5 (c (+ ?c 1)))
   (printout ?*output* "seat1: " ?seat2 " seat2: " (+ ?seat2 1) " Name1: " ?n2 " Name2: " ?g2 crlf)
   (modify ?f1 (state make_path)))

;;; *********
;;; make_path
;;; *********

(defrule make_path
   (context (state make_path))
   (seating (id ?id) (pid ?pid) (path_done no))
   (path (id ?pid) (name ?n1) (seat ?s))
   (not (path (id ?id) (name ?n1)))
   =>
   (assert (path (id ?id) (name ?n1) (seat ?s))))

;;; *********
;;; path_done
;;; *********

(defrule path_done
   ?f1 <- (context (state make_path))
   ?f2 <- (seating (path_done no))
   =>
   (modify ?f2 (path_done yes))
   (modify ?f1 (state check_done)))

;;; ********
;;; continue
;;; ********

(defrule continue
   ?f1 <- (context (state check_done))
   =>
   (modify ?f1 (state assign_seats))
)

;;; ***********
;;; are_we_done
;;; ***********

(defrule are_we_done
   ?f1 <- (context (state check_done))
   (last_seat (seat ?l_seat))
   (seating (seat2 ?l_seat))
   =>
   (printout ?*output* crlf "Yes, we are done!!" crlf)
   (modify ?f1 (state print_results)))

;;; *************
;;; print_results
;;; *************

(defrule print_results
   (context (state print_results))
   (seating (id ?id) (seat2 ?s2))
   (last_seat (seat ?s2))
   ?f4 <- (path (id ?id) (name ?n) (seat ?s))
   =>
   (retract ?f4)
   (printout ?*output* "Seat: " ?s " Name: " ?n  crlf))

;;; ********
;;; all_done
;;; ********

(defrule all_done
   (context (state print_results))
   =>
(printout t "ALL DONE")
)

   
   
   
   
   
   
   
(assert 
(guest (name n1) (sex m) (hobby h3))
(guest (name n1) (sex m) (hobby h2))

(guest (name n1) (sex m) (hobby h4))
(guest (name n1) (sex m) (hobby h5))
(guest (name n1) (sex m) (hobby h6))

(guest (name n2) (sex m) (hobby h2))
(guest (name n2) (sex m) (hobby h3))


(guest (name n3) (sex m) (hobby h1))
(guest (name n3) (sex m) (hobby h2))
(guest (name n3) (sex m) (hobby h3))

(guest (name n4) (sex f) (hobby h3))
(guest (name n4) (sex f) (hobby h2))

(guest (name n4) (sex f) (hobby h5))
(guest (name n4) (sex f) (hobby h6))

(guest (name n5) (sex f) (hobby h1))
(guest (name n5) (sex f) (hobby h2))
(guest (name n5) (sex f) (hobby h3))

(guest (name n6) (sex f) (hobby h3))
(guest (name n6) (sex f) (hobby h1))
(guest (name n6) (sex f) (hobby h2))

(guest (name n7) (sex f) (hobby h3))
(guest (name n7) (sex f) (hobby h2))

(guest (name n8) (sex m) (hobby h3))
(guest (name n8) (sex m) (hobby h1))

(last_seat (seat 8))
(count (c 1))
(context (state start)))
   