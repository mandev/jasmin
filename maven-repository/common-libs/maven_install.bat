@echo OFF

REM see : http://stackoverflow.com/questions/4955635/how-to-add-local-jar-files-in-maven-project

echo Install DIVA
cmd /C mvn install:install-file -Dfile=diva.jar -DgroupId=diva -DartifactId=diva -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
echo.

echo Install JAZZY
cmd /C mvn install:install-file -Dfile=jazzy.jar -DgroupId=jazzy -DartifactId=jazzy -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
echo.

echo Install L2FPROD
cmd /C mvn install:install-file -Dfile=l2fprod-common-all.jar -DgroupId=l2fprod -DartifactId=l2fprod-common-all -Dversion=7.3 -Dpackaging=jar -DgeneratePom=true
echo.

echo Install HIGLAYOUT
cmd /C mvn install:install-file -Dfile=higlayout.jar -DgroupId=higlayout -DartifactId=higlayout -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
echo.

echo Install PBTRANSFORM
cmd /C mvn install:install-file -Dfile=PBTransform.jar -DgroupId=PBTransform -DartifactId=PBTransform -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
echo. 

echo Install TASKDIALOG
cmd /C mvn install:install-file -Dfile=task-dialog-1.3.5.jar -DgroupId=task-dialog -DartifactId=task-dialog -Dversion=1.3.5 -Dpackaging=jar -DgeneratePom=true
echo. 