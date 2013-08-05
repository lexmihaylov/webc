webc
====

Tool for inlining scripts and styles in the html page of an web app

Usage
=====

```bash
java -jar webc.jar /path/to/file.html
```

This command will inline all scripts and styles that are included in file.html and output the html to the standard output

```bash
java -jar webc.jar /path/to/file.html > /path/to/inline.html
```
This command will inline all scripts and styles that are included in file.html and output the html inline.html
