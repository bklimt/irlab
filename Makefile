
#COLON=`(echo -n '$(CLASSPATH)' | sed -e 's/[^;]//g' && echo -n '$(CLASSPATH)' | sed -e 's/[^:]//g') | sed -e 's/^\(.\).*$$/\1/g'`

#JAVA=CLASSPATH="obj$(COLON)$(CLASSPATH)" java

JAVA=java -cp "obj:$$CLASSPATH"

all:
	cd src && make all

warnings:
	cd src && make warnings

clean:
	cd src && make clean

labels:
	java -Xmx1G edu.cmu.minorthird.ui.EditLabels -edit data/labels.txt -labels data/split/lectures.bsh

seminars: all
	$(JAVA) SeminarLabeller

questions: all
	$(JAVA) QuestionWriter

extractor: all
	$(JAVA) -Xmx1G SeminarExtractor >data/extractor.html

qa: all
	$(JAVA) -Xmx1G QADebugger

mixup: all
	$(JAVA) -Xmx1G MixupAnnotator test.xml

eval: all
	$(JAVA) -Xmx1G Evaluator


