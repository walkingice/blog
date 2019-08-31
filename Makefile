hexo=npx hexo

all: help

server:		## Run hexo server
	$(hexo) s

.PHONY: generate
generate:	## Generate
	$(hexo) g

.PHONY: deploy
deploy:		## generate then deploy
	$(hexo) d -g

.PHONY: help
help:		## Show this help.
	$(hexo) help
	@fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$$//' | sed -e 's/##//'
