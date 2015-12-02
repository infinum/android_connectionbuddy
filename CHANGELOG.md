Changelog
=========

## Version 1.0.6

_2015_12_01

* New : library has undergone rebranding process and the name has changed - it's ConnectionBuddy now. This also means that library has
        migrated to a new jCenter repository, and old one has been deleted.

## Version 1.0.5

_2015_12_01

* New : added new configuration options - you can now decide do you want to be notified about current network connection state
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