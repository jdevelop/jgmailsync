X-Spam-Checker-Version: SpamAssassin 3.2.1 (2007-05-02) on office.redwerk.com
X-Spam-Level: 
X-Spam-Status: No, score=0.0 required=5.0 tests=none autolearn=failed
	version=3.2.1
Received: from [::1] (helo=office.redwerk.com)
	by office.redwerk.com with esmtp (Exim 4.67 (FreeBSD))
	(envelope-from <users-return-2030-bofh=redwerk.com@javacc.dev.java.net>)
	id 1IS5OX-0000IZ-8y
	for bofh@localhost; Mon, 03 Sep 2007 09:26:45 +0300
X-Original-To: bofh@redwerk.com
Delivered-To: bofh@redwerk.com
Received: from redwerk.com [69.61.72.191]
	by office.redwerk.com with IMAP (fetchmail-6.3.8)
	for <bofh@localhost> (single-drop); Mon, 03 Sep 2007 09:26:45 +0300 (EEST)
Received: from dev.java.net (s015.sjc.collab.net [204.16.104.198])
	by redwerk.com (Postfix) with SMTP id A73ED119014
	for <bofh@redwerk.com>; Sat,  1 Sep 2007 03:14:49 +0200 (CEST)
Received: (qmail 12021 invoked by uid 5000); 1 Sep 2007 01:14:47 -0000
Mailing-List: contact users-help@javacc.dev.java.net; run by ezmlm
Precedence: bulk
X-No-Archive: yes
list-help: <mailto:users-help@javacc.dev.java.net>
list-unsubscribe: <mailto:users-unsubscribe@javacc.dev.java.net>
list-post: <mailto:users@javacc.dev.java.net>
Reply-To: users@javacc.dev.java.net
Delivered-To: mailing list users@javacc.dev.java.net
Received: (qmail 12004 invoked from network); 1 Sep 2007 01:14:47 -0000
X-IronPort-Anti-Spam-Filtered: true
X-IronPort-Anti-Spam-Result: AgAAAC5a2EbOL8eln2dsb2JhbACOBwEBAgcEBgcIGA
X-IronPort-AV: E=Sophos;i="4.20,195,1186383600"; 
   d="scan'208";a="81730889"
X-IRONPORT: SCANNED
Message-ID: <46D8BD6B.6020405@engr.mun.ca>
Date: Fri, 31 Aug 2007 22:46:27 -0230
From: "Theodore S. Norvell" <theo@engr.mun.ca>
User-Agent: Thunderbird 2.0.0.6 (Windows/20070728)
MIME-Version: 1.0
To:  users@javacc.dev.java.net
References: <839ba01c0708262138i4232c1c3w88e9355e29b7fe34@mail.gmail.com>	 <839ba01c0708270126u206a1b31m2898a648bf3bf60c@mail.gmail.com>	 <d15ae0610708270545o32520028y999ed07fbd2f612b@mail.gmail.com>	 <839ba01c0708271957r4d94c0bvb6c7fae03c786c44@mail.gmail.com>	 <d15ae0610708280100u7b81d85eo3b95b23d7f0f026b@mail.gmail.com>	 <839ba01c0708280320n3b9b6c44l75d823b4c9eb9d69@mail.gmail.com>	 <1188306521.21853.12.camel@bugs.hal> <839ba01c0708281931r5205f31fnec7139404c4cd903@mail.gmail.com>
In-Reply-To: <839ba01c0708281931r5205f31fnec7139404c4cd903@mail.gmail.com>
Content-Type: text/plain; charset=UTF-8; format=flowed
Content-Transfer-Encoding: 7bit
Subject: Re: [JavaCC] Re: Questions on matching nested open, close brackets

Cedric:  What was 9 and what was 11? I'm guessing that they are both ")".
Is it possible that the token manager assigns different token types to
the same r.e. if that r.e. appears in different states?  I'm putting my 
money
on that.  Can anyone confirm this?

Take a look at FAQ 4.16:
      In general when a regular expression [that appears in a BNF rule] 
is a Java string and identical to
      regular expression occurring in a regular expression production 
[Footnote: And provided that
      regular expression applies in the DEFAULT lexical state.], then 
the Java string is interchangeable
      with the token kind from the regular expression production.
See also the footnote in 4.17.

It took me a while to find these in the FAQ as really this is a token 
manager issue, but the FAQ covers it in the
parser section.

So why don't we issue a warning when regular expressions productions (a) 
do not apply in DEFAULT,
(b) are nameless, (c) are not SKIP or MORE?

Cheers,
Theo
>
> I fixed it by assigning names to the tokens "(" and ")":
> <IN_TERM> TOKEN : {
> 	<OPEN: "("> {tokenDepth += 1; }
> |
> 	<CLOSE: ")"> { tokenDepth -= 1;
> 		SwitchTo(tokenDepth==0 ? DEFAULT : IN_TERM);
> 	}
> |
> 	<TERM: ~["\t", "\n", "\r", "(", ")"] (~["\t", "\n", "\r", "(", ")"])*>
> }
>
> I still do not understand why I need to give them names to make their
> token.type correct. But anyway, thanks a lot for helping me through
> this.
>   

---------------------------------------------------------------------
To unsubscribe, e-mail: users-unsubscribe@javacc.dev.java.net
For additional commands, e-mail: users-help@javacc.dev.java.net

