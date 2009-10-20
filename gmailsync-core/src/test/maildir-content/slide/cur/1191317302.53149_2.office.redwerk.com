X-Spam-Checker-Version: SpamAssassin 3.2.1 (2007-05-02) on office.redwerk.com
X-Spam-Level: 
X-Spam-Status: No, score=-4.0 required=5.0 tests=RCVD_IN_DNSWL_MED
	autolearn=failed version=3.2.1
Received: from [::1] (helo=office.redwerk.com)
	by office.redwerk.com with esmtp (Exim 4.67 (FreeBSD))
	(envelope-from <slide-user-return-13800-bofh=redwerk.com@jakarta.apache.org>)
	id 1Ice3C-000DpB-8Y
	for bofh@localhost; Tue, 02 Oct 2007 12:28:22 +0300
Received: from mail.redwerk.com [89.105.196.9]
	by office.redwerk.com with IMAP (fetchmail-6.3.8)
	for <bofh@localhost> (single-drop); Tue, 02 Oct 2007 12:28:22 +0300 (EEST)
Received: from hermes.apache.org ([140.211.11.2] helo=mail.apache.org)
	by redwerk.com with smtp (Exim 4.60)
	(envelope-from <slide-user-return-13800-bofh=redwerk.com@jakarta.apache.org>)
	id 1Ice25-0004pV-6f
	for bofh@redwerk.com; Tue, 02 Oct 2007 11:27:13 +0200
Received: (qmail 70505 invoked by uid 500); 2 Oct 2007 09:27:01 -0000
Mailing-List: contact slide-user-help@jakarta.apache.org; run by ezmlm
Precedence: bulk
List-Unsubscribe: <mailto:slide-user-unsubscribe@jakarta.apache.org>
List-Help: <mailto:slide-user-help@jakarta.apache.org>
List-Post: <mailto:slide-user@jakarta.apache.org>
List-Id: "Slide Users Mailing List" <slide-user.jakarta.apache.org>
Reply-To: "Slide Users Mailing List" <slide-user@jakarta.apache.org>
Delivered-To: mailing list slide-user@jakarta.apache.org
Received: (qmail 70494 invoked by uid 99); 2 Oct 2007 09:27:01 -0000
Received: from athena.apache.org (HELO athena.apache.org) (140.211.11.136)
    by apache.org (qpsmtpd/0.29) with ESMTP; Tue, 02 Oct 2007 02:27:01 -0700
X-ASF-Spam-Status: No, hits=1.2 required=10.0
	tests=SPF_NEUTRAL
Received-SPF: neutral (athena.apache.org: local policy)
Received: from [195.23.133.215] (HELO mailrly05.isp.novis.pt) (195.23.133.215)
    by apache.org (qpsmtpd/0.29) with ESMTP; Tue, 02 Oct 2007 09:26:59 +0000
Received: (qmail 28492 invoked from network); 2 Oct 2007 09:26:24 -0000
Received: from unknown (HELO mailfrt07.isp.novis.pt) ([195.23.133.199])
          (envelope-sender <mfigueiredo@maisis.pt>)
          by mailrly05.isp.novis.pt with compressed SMTP; 2 Oct 2007 09:26:24 -0000
Received: (qmail 8323 invoked from network); 2 Oct 2007 09:26:23 -0000
Received: from unknown (HELO www.maisis.pt) ([194.79.73.103])
          (envelope-sender <mfigueiredo@maisis.pt>)
          by mailfrt07.isp.novis.pt with SMTP; 2 Oct 2007 09:26:23 -0000
Received: from maisis08 (unknown [172.27.192.196])
	by www.maisis.pt (Postfix) with ESMTP id 01E74FDC4
	for <slide-user@jakarta.apache.org>; Tue,  2 Oct 2007 10:26:01 +0100 (WEST)
From: "Miguel Figueiredo" <mfigueiredo@maisis.pt>
To: "'Slide Users Mailing List'" <slide-user@jakarta.apache.org>
References: <A9527F0C2C737446872554EBEF28D1FD01810F60@OASBURBEX01.oaifield.onasgn.com> <001401c801b1$e55e49a0$c4c01bac@maisis.local> <A9527F0C2C737446872554EBEF28D1FD01810FC4@OASBURBEX01.oaifield.onasgn.com>
Subject: RE: PROPFIND 409 conflict ERROR with timeout/deadlock
Date: Tue, 2 Oct 2007 10:26:00 +0100
Message-ID: <009b01c804d6$3ea67d30$c4c01bac@maisis.local>
MIME-Version: 1.0
Content-Type: text/plain;
	charset="us-ascii"
Content-Transfer-Encoding: 7bit
X-Mailer: Microsoft Office Outlook 11
X-MimeOLE: Produced By Microsoft MimeOLE V6.00.2900.3138
In-Reply-To: <A9527F0C2C737446872554EBEF28D1FD01810FC4@OASBURBEX01.oaifield.onasgn.com>
Thread-Index: AcgBVfIPjvDQVNtmSh2xiDg49VtY1gAWpVAgAAxrH+AAvOhY8A==
X-maisis-MailScanner: Found to be clean
X-maisis-MailScanner-From: mfigueiredo@maisis.pt
X-Virus-Checked: Checked by ClamAV on apache.org

Hello again Shakti,

 Maybe you can't start a nested transaction like it happens in the "no"
branch of point 3 and 4 (the 1st transaction happens on point 2).

Hope this helps,
Miguel Figueiredo


-----Original Message-----
From: Shakti Shrivastava [mailto:Shakti.Shrivastava@onassignment.com] 
Sent: sexta-feira, 28 de Setembro de 2007 16:23
To: Slide Users Mailing List
Subject: RE: PROPFIND 409 conflict ERROR with timeout/deadlock

Hi Miguel,

That fixed the issue. Thank you very much. But I still don't understand
why? Can Oliver chime in? I would really want to know.

This is what the code is doing (while trying to upload a resource like
http://host:port/slide/files/user-75/myfile.txt

1. Find collection files (this is http://host:port/slide/files)
2. If found add to a list (cache) and start a transaction (explicitly,
using startTransaction())
3. Check if collection user-57 exists using getChildren() if so add to
list (cache). No (explicit) transaction started here
4. Check if myfile.txt exists. No (explicit) transaction started here *
this is where the timeout is occurring 
5. If no then use putMethod()
6. Complete with commitTransaction()
7. in case of error do abortTransaction()

As you see there isn't an issue of nested transactions. Again, thanks
for your input.


-----Original Message-----
From: Miguel Figueiredo [mailto:mfigueiredo@maisis.pt] 
Sent: Friday, September 28, 2007 2:28 AM
To: 'Slide Users Mailing List'
Subject: RE: PROPFIND 409 conflict ERROR with timeout/deadlock

Hello Shakti,

 Although I'm not familiar with the details (Oliver would), you will be
happy to know that Slide already wraps write requests in internal
transactions. Checkout your Domain.xml, there is a piece of
configuration
that implicitly says just that:

<!-- "false" lets all read-only methods be executed outside of
transactions
-->
<parameter name="all-methods-in-transactions">false</parameter>

 From here http://www.checkupdown.com/status/E409.html I deduct that you
might be doing something illegal from the point of view of Slide WebApp.
What can it be?

 Hope this helps,
 Miguel Figueiredo

-----Original Message-----
From: Shakti Shrivastava [mailto:Shakti.Shrivastava@onassignment.com] 
Sent: quinta-feira, 27 de Setembro de 2007 23:30
To: slide-user@jakarta.apache.org
Subject: PROPFIND 409 conflict ERROR with timeout/deadlock

I am getting 409 conflict error when trying to create a WebdavResource
object for a given URL. 

The code is trying to create a file along with any missing collections
(that may not exist). Everything works fine however the code seems to
deadlock when trying to create a WebdavResource object on the filename.
I am using startTransaction and commitTransaction to make sure the file
upload either completes successfully or fails completely. The code runs
fine, in that the file upload completes, but I want to understand the
reason of the timeout here and get rid of it. Can someone please shed
some light on this?

I have googled this and nowhere can I find 409 status code associated
with a PROPFIND. This leads me to believe that this timeout has
something to do with a lock. However, nowhere in my code am I explicitly
locking any resource (may be it's the startTransaction()?).

Thanks in advance.

Using:

Tomcat 5.5.x
Slide version 2.2 (from SVN)
JVM: 1.5.x

 

Here are the logs from the SLIDE server (2 lines prefixed with * are the
ones where the error is happening)

http-8081-Processor25, 25-Sep-2007 18:50:08, unauthenticated, GET, 404
"Not Found", 547 ms, /files/user-docs/user-57
http-8081-Processor24, 25-Sep-2007 18:50:31, unauthenticated, PROPFIND,
404 "Not Found", 781 ms, /files/user-docs/user-57
http-8081-Processor25, 25-Sep-2007 18:50:32, unauthenticated, PROPFIND,
207 "Multi-Status", 125 ms, /files
http-8081-Processor25, 25-Sep-2007 18:50:32, unauthenticated, LOCK, 200
"OK", 31 ms, /files
25 Sep 2007 18:50:32 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor25, 25-Sep-2007 18:50:32, unauthenticated, PROPFIND,
207 "Multi-Status", 63 ms, /files
25 Sep 2007 18:50:32 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor25, 25-Sep-2007 18:50:32, unauthenticated, MKCOL, 201
"Created", 219 ms, /files/user-docs/user-57
25 Sep 2007 18:50:33 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor25, 25-Sep-2007 18:50:33, unauthenticated, PROPFIND,
207 "Multi-Status", 281 ms, /files/3
25 Sep 2007 18:50:33 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor25, 25-Sep-2007 18:50:33, unauthenticated, PROPFIND,
207 "Multi-Status", 0 ms, /files/user-docs/user-57
********** 25 Sep 2007 18:52:33 - file-meta-store - INFO - DEADLOCK
VICTIM: Thread Thread[http-8081-Processor23,5,main] marked transaction
branch http-8081-Processor23-1190771433584-32 for rollback
********** http-8081-Processor23, 25-Sep-2007 18:50:33, unauthenticated,
PROPFIND, 409 "Conflict", 120063 ms, /files/user-docs/user-57/myfile.txt
25 Sep 2007 18:52:33 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor24, 25-Sep-2007 18:52:33, unauthenticated, CHECKOUT,
404 "Not Found", 31 ms, /files/user-docs/user-57/myfile.txt
25 Sep 2007 18:52:33 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor24, 25-Sep-2007 18:52:33, unauthenticated, GET, 404
"Not Found", 15 ms, /files/user-docs/user-57/myfile.txt
25 Sep 2007 18:52:33 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor24, 25-Sep-2007 18:52:33, unauthenticated, PUT, 201
"Created", 109 ms, /files/user-docs/user-57/myfile.txt
25 Sep 2007 18:52:34 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor24, 25-Sep-2007 18:52:34, unauthenticated, PROPPATCH,
207 "Multi-Status", 47 ms, /files/user-docs/user-57/myfile.txt
25 Sep 2007 18:52:34 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor24, 25-Sep-2007 18:52:34, unauthenticated, PROPPATCH,
207 "Multi-Status", 0 ms, /files/user-docs/user-57/myfile.txt
25 Sep 2007 18:52:34 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor24, 25-Sep-2007 18:52:34, unauthenticated, PROPPATCH,
207 "Multi-Status", 0 ms, /files/user-docs/user-57/myfile.txt
25 Sep 2007 18:52:34 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor24, 25-Sep-2007 18:52:34, unauthenticated, PROPPATCH,
207 "Multi-Status", 47 ms, /files/user-docs/user-57/myfile.txt
25 Sep 2007 18:52:34 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor24, 25-Sep-2007 18:52:34, unauthenticated, PROPPATCH,
207 "Multi-Status", 0 ms, /files/user-docs/user-57/myfile.txt
25 Sep 2007 18:52:34 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor24, 25-Sep-2007 18:52:34, unauthenticated, PROPPATCH,
207 "Multi-Status", 31 ms, /files/user-docs/user-57/myfile.txt
25 Sep 2007 18:52:34 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor24, 25-Sep-2007 18:52:34, unauthenticated, PROPPATCH,
207 "Multi-Status", 15 ms, /files/user-docs/user-57/myfile.txt
25 Sep 2007 18:52:34 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor24, 25-Sep-2007 18:52:34, unauthenticated, PROPPATCH,
207 "Multi-Status", 0 ms, /files/user-docs/user-57/myfile.txt
25 Sep 2007 18:52:34 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor24, 25-Sep-2007 18:52:34, unauthenticated, PROPPATCH,
207 "Multi-Status", 31 ms, /files/user-docs/user-57/myfile.txt
25 Sep 2007 18:52:34 -
org.apache.slide.webdav.method.AbstractWebdavMethod - INFO - Using
external transaction <opaquelocktoken:97995b34f21391315f7f53107fefe0d0>
http-8081-Processor24, 25-Sep-2007 18:52:34, unauthenticated,
VERSION-CONTROL, 200 "OK", 453 ms, /files/user-docs/user-57/myfile.txt
http-8081-Processor24, 25-Sep-2007 18:52:35, unauthenticated, UNLOCK,
204 "No Content", 156 ms, /files/user-docs/user-57

---------------------------------------------------------------------
To unsubscribe, e-mail: slide-user-unsubscribe@jakarta.apache.org
For additional commands, e-mail: slide-user-help@jakarta.apache.org


---------------------------------------------------------------------
To unsubscribe, e-mail: slide-user-unsubscribe@jakarta.apache.org
For additional commands, e-mail: slide-user-help@jakarta.apache.org


---------------------------------------------------------------------
To unsubscribe, e-mail: slide-user-unsubscribe@jakarta.apache.org
For additional commands, e-mail: slide-user-help@jakarta.apache.org


---------------------------------------------------------------------
To unsubscribe, e-mail: slide-user-unsubscribe@jakarta.apache.org
For additional commands, e-mail: slide-user-help@jakarta.apache.org


