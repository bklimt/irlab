
JAVAC=mkdir -p ../obj && javac -classpath "../obj:$$CLASSPATH" -d ../obj -nowarn -g
CC=mkdir -p ../obj && gcc -c
CCL=mkdir -p ../bin && gcc

all: ../obj/SeminarLabeller.class ../obj/QuestionWriter.class ../obj/SeminarExtractor.class ../obj/AnnotationDumper.class ../obj/QASystem.class ../obj/QADebugger.class ../obj/Evaluator.class ../obj/CRFExperiment.class ../bin/ParseQuestion

warnings:
	make JAVAC="mkdir -p ../obj && javac -d ../obj -Xlint:deprecation -Xlint:unchecked" all

clean:
	rm ../obj/*.class 2>/dev/null || true
	rm ../obj/*.o 2>/dev/null || true
	rm ../bin/* 2>/dev/null || true

../obj/HighlightManager.class: HighlightManager.java
	$(JAVAC) $<

../obj/EmailRepository.class: EmailRepository.java
	$(JAVAC) $<

../obj/Annotation.class: Annotation.java
	$(JAVAC) $<

../obj/DateTime.class: DateTime.java
	$(JAVAC) $<

../obj/Location.class: Location.java
	$(JAVAC) $<

../obj/DateTimeParser.class: DateTimeParser.java ../obj/DateTime.class
	$(JAVAC) $<

../obj/LocationParser.class: LocationParser.java ../obj/Location.class
	$(JAVAC) $<

../obj/Seminar.class: Seminar.java ../obj/Annotation.class ../obj/DateTimeParser.class ../obj/LocationParser.class
	$(JAVAC) $<

../obj/SeminarDatabase.class: SeminarDatabase.java ../obj/Seminar.class ../obj/SeminarRetriever.class
	$(JAVAC) $<

../obj/SeminarEvaluator.class: SeminarEvaluator.java ../obj/Seminar.class
	$(JAVAC) $<

../obj/EmailAnnotator.class: EmailAnnotator.java ../obj/Annotation.class ../obj/EmailRepository.class
	$(JAVAC) $<

../obj/Search.class: Search.java ../obj/EmailAnnotator.class ../obj/EmailRepository.class
	$(JAVAC) $<

../obj/ManualEmailAnnotator.class: ManualEmailAnnotator.java ../obj/EmailAnnotator.class
	$(JAVAC) $<

../obj/MixupAnnotator.class: MixupAnnotator.java ../obj/EmailAnnotator.class
	$(JAVAC) $<

../obj/NewSeminarFrame.class: NewSeminarFrame.java ../obj/Annotation.class ../obj/SeminarDatabase.class
	$(JAVAC) $<

../obj/SeminarLabeller.class: SeminarLabeller.java ../obj/ManualEmailAnnotator.class ../obj/SeminarDatabase.class ../obj/NewSeminarFrame.class ../obj/HighlightManager.class
	$(JAVAC) $<

../obj/SeminarExtractor.class: SeminarExtractor.java ../obj/ManualEmailAnnotator.class ../obj/SeminarDatabase.class ../obj/SeminarEvaluator.class ../obj/EmailRepository.class ../obj/SeminarRetriever.class
	$(JAVAC) $<

../obj/SeminarRetriever.class: SeminarRetriever.java ../obj/EmailAnnotator.class
	$(JAVAC) $<

../obj/AnswerSet.class: AnswerSet.java
	$(JAVAC) $<

../obj/AnswerExtractor.class: AnswerExtractor.java ../obj/AnswerSet.class
	$(JAVAC) $<

../obj/Question.class: Question.java
	$(JAVAC) $<

../obj/QuestionManager.class: QuestionManager.java ../obj/Question.class ../obj/AnswerExtractor.class
	$(JAVAC) $<

../obj/QuestionWriter.class: QuestionWriter.java ../obj/ManualEmailAnnotator.class ../obj/SeminarDatabase.class ../obj/HighlightManager.class ../obj/QuestionManager.class
	$(JAVAC) $<

../obj/AnnotationDumper.class: AnnotationDumper.java ../obj/ManualEmailAnnotator.class
	$(JAVAC) $<

../obj/Query.class: Query.java ../obj/Seminar.class ../obj/Lisp.class
	$(JAVAC) $<

../obj/Lisp.class: Lisp.java
	$(JAVAC) $<

../obj/QuestionAnalyzer.class: QuestionAnalyzer.java ../obj/Query.class ../obj/Lisp.class
	$(JAVAC) $<

../obj/NLAnswerExtractor.class: NLAnswerExtractor.java ../obj/EmailRepository.class ../obj/AnswerExtractor.class ../obj/SeminarRetriever.class ../obj/QuestionAnalyzer.class ../obj/Query.class ../obj/Lisp.class
	$(JAVAC) $<

../obj/StatisticalAnswerExtractor.class: StatisticalAnswerExtractor.java ../obj/Search.class ../obj/AnswerExtractor.class
	$(JAVAC) $<

../obj/QAController.class: QAController.java ../obj/StatisticalAnswerExtractor.class ../obj/NLAnswerExtractor.class ../obj/QuestionAnalyzer.class ../obj/SeminarExtractor.class ../obj/SeminarDatabase.class ../obj/ManualEmailAnnotator.class ../obj/EmailRepository.class ../obj/MixupAnnotator.class ../obj/QuestionManager.class
	$(JAVAC) $<

../obj/QASystem.class: QASystem.java ../obj/QAController.class
	$(JAVAC) $<

../obj/QADebugger.class: QADebugger.java ../obj/QAController.class ../obj/HighlightManager.class
	$(JAVAC) $<

../obj/Evaluator.class: Evaluator.java ../obj/QAController.class
	$(JAVAC) $<

../obj/CRFExperiment.class: CRFExperiment.java
	$(JAVAC) $<

../obj/ParseQuestion.o: ParseQuestion.cpp
	$(CC) -c -o $@ $<

../bin/ParseQuestion: ../obj/ParseQuestion.o
	$(CCL) -o $@ $< -lstdc++

