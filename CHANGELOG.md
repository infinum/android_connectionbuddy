Changelog
=========

## Version 1.4.0
_2016-12_07_

* Update: compileSdkVersion to API version 25
* New: connect to WiFi signal by providing network SSID and network password

## Version 1.3.0
_2016-10_13_

* Update: compileSdkVersion to API version 24
* Update: minSdkVersion is now API version 9 (min supported version of Android Support Library)

## Version 1.2.0

_2016-09-30_

* Update: Library is now backported to API level 8.

## Version 1.1.2

_2016-07-19_

* Fixes: fixed null pointer exceptions in getting signal strength

## Version 1.1.1

_2016-07-14_

* Update: bumped SDK version to 23
* Update: removed ConnectivityType.BOTH, as device can have only one ConnectivityType at the moment
* Update: code hygiene and refactor

## Version 1.1.0

_2016-03-17_

* New: introduced ConnectionBuddyActivity for simpler configuration
* New: added option to be notified only about reliable events
* Update: code hygiene and refactor

## Version 1.0.6

_2015-12-01_

* New: library has undergone rebranding process and the name has changed - it's ConnectionBuddy now. This also means that library has
        migrated to a new jCenter repository, and the old one has been deleted.

## Version 1.0.5

_2015-12-01_

* New: added new configuration options - you can now decide do you want to be notified about current network connection state
        immediately after the listener has been registered

## Version 1.0.4

_2015-11-15_

* Update: refactored caching mechanism to use LruCache instead of SharedPreferences

## Version 1.0.3.

_2015-10-11_

* Bug fixes - fixed bug with signal strength

## Version 1.0.2.
_2015-10-31_

* New: added information about signal strength to ConnectivityEvent object
* Update: events can now also be filtered by signal strength

## Version 1.0.1.

_2015-10-19_

* New: added information about network connection type to network change receiver
* New: library behaviour can now be customized - we can decide for which network connection event type we want to register for
* Update: unified all names of properties
* Bug fixes 

## Version 1.0.0

_2015-09-21_

Initial release.
