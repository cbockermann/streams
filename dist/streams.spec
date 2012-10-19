Name:		streams
Version:	%_version
Release:	%_revision
Summary:	The streams framework
Group:		admin
License:	AGPL
URL:		http://www.jwall.org/streams/
Source0:	http://www.jwall.org/download/jwall-tools/jwall-tools-latest-src.zip
BuildRoot:	/home/chris/Projekte/streams

%description
Brief description of software package.

%prep

%build

%install

%clean

%post
chmod 755 /opt/streams/bin/*
rm /usr/bin/stream.run
ln -s /opt/streams/bin/stream.run /usr/bin/stream.run

%preun
rm /usr/bin/stream.run

%files -f rpmfiles.list
%defattr(-,root,root)
%doc

%changelog
