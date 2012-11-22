
(defun repr (s)
	(cond
		((symbolp s) (symbol-name s))
		((numberp s) (format nil "~d" s))
		(t s)))

(defun join (lst)
	(cond
		((null lst) "")
		((null (cdr lst)) (repr (car lst)))
		(t (concatenate 'string (repr (car lst)) " " (join (cdr lst))))))

(defun tail (lst)
  	(cond
	  	((null lst) nil)
		((null (cdr lst)) (car lst))
		(t (tail (cdr lst)))))

(load "lisp/v8-4-cl.lisp")
(compgra "lisp/grammar")

(defun parse-question (question)
  (print question)
  (parse-list (append question '($))))

(setq question (read-from-string (tail si::*command-args*) ")"))

(print question)

(parse-question question)

;(load "questions.lisp")
;(defun parse-questions (questions)
;  (cond
;    ((not (null questions))  (parse-question (car questions))
;                             (parse-questions (cdr questions)))))
;(parse-questions *questions*)

(quit)

