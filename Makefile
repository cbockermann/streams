VERSION=0.9.6-SNAPSHOT
REVISION=8
NAME=streams
BUILD=.build_tmp
DIST=jwall-devel
ZIP_FILE=${NAME}-${VERSION}-${REVISION}.zip
DEB_FILE=${NAME}-${VERSION}-${REVISION}.deb
RPM_FILE=${NAME}-${VERSION}-${REVISION}.noarch.rpm
RELEASE_DIR=releases
RPMBUILD=$(PWD)/.rpmbuild
ARCH=noarch
MODULES = stream-api stream-core stream-runtime

update-license:
	find stream-api -name '*.java' -exec perl src/main/scripts/prepend.pl {} LICENSE.preamble.txt \;
	find stream-core -name '*.java' -exec perl src/main/scripts/prepend.pl {} LICENSE.preamble.txt \;
	find stream-runtime -name '*.java' -exec perl src/main/scripts/prepend.pl {} LICENSE.preamble.txt \;

plugin:
	@cd stream-plugin && make plugin

install-plugin:
	@cd stream-plugin && make install

clean:
	@mvn clean
	@cd stream-plugin && make clean



pre-package:
	echo "Preparing packages build in ${BUILD}"
	mkdir -p ${BUILD}
	mkdir -p ${BUILD}/opt/streams/lib
	mkdir -p ${BUILD}/opt/streams/plugins
	cp -a dist/opt ${BUILD}/
	mvn -DskipTests=true clean install
	rm -rf stream-runner/target/dependency/*
	cd stream-runner && mvn -DskipTests=true dependency:copy-dependencies && cd ..
	cp stream-runner/target/dependency/*.jar ${BUILD}/opt/streams/lib/
#
#	cp stream-api/target/stream-api-${VERSION}.jar ${BUILD}/opt/streams/lib
#	for mod in ${MODULES} ; do \
#		cd $$mod && mvn -DskipTests=true package ; \
#		cd .. ; \
#		cp $$mod/target/dependency/*.jar ${BUILD}/opt/streams/lib/ ; \
#		cp $$mod/target/$$mod-${VERSION}.jar ${BUILD}/opt/streams/lib/ ; \
#	done

zip:	pre-package
	cd ${BUILD} && zip -r ../${RELEASE_DIR}/${NAME}-${VERSION}-${REVISION}.zip . && cd ..
	md5sum ${RELEASE_DIR}/${ZIP_FILE} > ${RELEASE_DIR}/${ZIP_FILE}.md5

deb:	pre-package
	rm -rf ${RELEASE_DIR}
	mkdir -p ${RELEASE_DIR}
	mkdir -p ${BUILD}/DEBIAN
	cp dist/DEBIAN/* ${BUILD}/DEBIAN/
	cat dist/DEBIAN/control | sed -e 's/Version:.*/Version: ${VERSION}-${REVISION}/' > ${BUILD}/DEBIAN/control
	chmod 755 ${BUILD}/DEBIAN/p*
	cd ${BUILD} && find opt -type f -exec md5sum {} \; > DEBIAN/md5sums && cd ..
	dpkg -b ${BUILD} ${RELEASE_DIR}/${DEB_FILE}
	md5sum ${RELEASE_DIR}/${DEB_FILE} > ${RELEASE_DIR}/${DEB_FILE}.md5
	rm -rf ${BUILD}
	debsigs --sign=origin --default-key=C5C3953C ${RELEASE_DIR}/${DEB_FILE}

release-deb:
	reprepro --ask-passphrase -b /var/www/download.jwall.org/htdocs/debian includedeb ${DIST} ${RELEASE_DIR}/${DEB_FILE}


unrelease-deb:
	reprepro --ask-passphrase -b /var/www/download.jwall.org/htdocs/debian remove ${DIST} streams
