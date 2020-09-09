import os
import subprocess
import shutil

shutil.rmtree("build", ignore_errors = True)
os.mkdir("build")
subprocess.call(["javac", "-cp", "lib/commons-math3-3.6.1.jar", "-d", "build", *[f for f in os.listdir(".") if os.path.splitext(f)[1] == ".java"]])

subprocess.call(["java", "UI", "-cp", "../lib/commons-math3-3.6.1.jar"], cwd = "build")