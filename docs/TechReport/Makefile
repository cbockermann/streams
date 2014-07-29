
NAME=TechReport

single:
	@pdflatex $(NAME) > /dev/null

all:
	@pdflatex $(NAME) > /dev/null
	@bibtex $(NAME) > /dev/null
	@pdflatex $(NAME) > /dev/null
	pdflatex $(NAME)

clean:
	@rm -f *.bbl *.blg *.aux *.out $(NAME).pdf $(NAME).log
