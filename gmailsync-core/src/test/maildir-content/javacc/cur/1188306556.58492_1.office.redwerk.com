X-Spam-Checker-Version: SpamAssassin 3.2.1 (2007-05-02) on office.redwerk.com
X-Spam-Level: 
X-Spam-Status: No, score=0.0 required=5.0 tests=none autolearn=failed
	version=3.2.1
Received: from [::1] (helo=office.redwerk.com)
	by office.redwerk.com with esmtp (Exim 4.67 (FreeBSD))
	(envelope-from <users-return-2027-bofh=redwerk.com@javacc.dev.java.net>)
	id 1IQ0ol-000FDM-OD
	for bofh@localhost; Tue, 28 Aug 2007 16:09:16 +0300
X-Original-To: bofh@redwerk.com
Delivered-To: bofh@redwerk.com
Received: from redwerk.com [69.61.72.191]
	by office.redwerk.com with IMAP (fetchmail-6.3.8)
	for <bofh@localhost> (single-drop); Tue, 28 Aug 2007 16:09:15 +0300 (EEST)
Received: from dev.java.net (s015.sjc.collab.net [204.16.104.198])
	by redwerk.com (Postfix) with SMTP id 08662119021
	for <bofh@redwerk.com>; Tue, 28 Aug 2007 15:08:57 +0200 (CEST)
Received: (qmail 22317 invoked by uid 5000); 28 Aug 2007 13:08:57 -0000
Mailing-List: contact users-help@javacc.dev.java.net; run by ezmlm
Precedence: bulk
X-No-Archive: yes
list-help: <mailto:users-help@javacc.dev.java.net>
list-unsubscribe: <mailto:users-unsubscribe@javacc.dev.java.net>
list-post: <mailto:users@javacc.dev.java.net>
Reply-To: users@javacc.dev.java.net
Delivered-To: mailing list users@javacc.dev.java.net
Received: (qmail 22301 invoked from network); 28 Aug 2007 13:08:56 -0000
X-IronPort-Anti-Spam-Filtered: true
X-IronPort-Anti-Spam-Result: AgAAAMO700ZIIDMceWdsb2JhbACNfQEBCQon
X-IronPort-AV: E=Sophos;i="4.19,316,1183359600"; 
   d="scan'208";a="81361595"
X-IRONPORT: SCANNED
From: Tom Copeland <tom@infoether.com>
To: users@javacc.dev.java.net
In-Reply-To: <839ba01c0708280320n3b9b6c44l75d823b4c9eb9d69@mail.gmail.com>
References: <839ba01c0708262138i4232c1c3w88e9355e29b7fe34@mail.gmail.com>
	 <839ba01c0708270126u206a1b31m2898a648bf3bf60c@mail.gmail.com>
	 <d15ae0610708270545o32520028y999ed07fbd2f612b@mail.gmail.com>
	 <839ba01c0708271957r4d94c0bvb6c7fae03c786c44@mail.gmail.com>
	 <d15ae0610708280100u7b81d85eo3b95b23d7f0f026b@mail.gmail.com>
	 <839ba01c0708280320n3b9b6c44l75d823b4c9eb9d69@mail.gmail.com>
Content-Type: text/plain
Date: Tue, 28 Aug 2007 09:08:41 -0400
Message-Id: <1188306521.21853.12.camel@bugs.hal>
Mime-Version: 1.0
X-Mailer: Evolution 2.8.0 (2.8.0-7.fc6) 
Content-Transfer-Encoding: 7bit
Subject: Re: [JavaCC] Re: Questions on matching nested open, close brackets

On Tue, 2007-08-28 at 18:20 +0800, Cedric Ho wrote:
> from the "JavaCC FAQ 3.17 How do I tokenize nested comments?"
> it seems that it is what I would needed. However when I try to follow
> its example, I encountered the following error during parsing:
> 
> Exception in thread "main"
> wisers.wisengp.parser.normal.ParseException: Encountered ")" at line
> 1, column 9.
> Was expecting one of:
>     "(" ...
>     <TERM> ...
>     ")" ...
> 
> I am pretty confused with the error message. Since I haven't change
> anything else yet. I just followed the example and added a Lexcial
> State IN_TERM. 

I think you're on the right path.  One thing I usually do when I start
hitting errors like this is ensure that the scanner is tokenizing
everything like I expect it to.  You can do this by just running your
TokenManager, e.g.:

============================
$ cat terms.jj 
options {
  BUILD_PARSER=false;
}
PARSER_BEGIN(Terms)
public class Terms {}
PARSER_END(Terms)
TOKEN_MGR_DECLS : {
  public static void main(String[] args) {
    String data = "AND(OR((term(with)bracket) (term with space))
((another)term with space))";
    TermsTokenManager mgr = new TermsTokenManager(new
SimpleCharStream(new java.io.StringReader(data)));
    Token t;
    while ((t = mgr.getNextToken()) != null) {
      debugStream.println("Token: " + t.image);
    }
  }
  private static int tokenDepth = 0;
}

TOKEN : {
  <AND: "AND("> | <OR: "OR("> | <AND_NOT: "AND_NOT(">
  | "(" {tokenDepth = 1;} : IN_TERM
}

<IN_TERM> TOKEN : {
  "(" {tokenDepth += 1; }
  | ")" { tokenDepth -= 1; SwitchTo(tokenDepth==0 ? DEFAULT :
IN_TERM); }
  | <TERM: ~["\t", "\n", "\r", "(", ")", " "] (~["\t", "\n", "\r", "(",
")"])*>
}
$ javacc terms.jj && javac *.java && java TermsTokenManager
Java Compiler Compiler Version 4.0 (Parser Generator)
(type "javacc" with no arguments for help)
Reading from file terms.jj . . .
Parser generated successfully.
Token: AND(
Token: OR(
Token: (
Token: term
Token: (
Token: with
Token: )
Token: bracket
Token: )
Exception in thread "main" TokenMgrError: Lexical error at line 1,
column 27.  Encountered: " " (32), after : ""
        at TermsTokenManager.getNextToken(TermsTokenManager.java:453)
        at TermsTokenManager.main(TermsTokenManager.java:9)
============================

You can see here that a token is being created for the left parenthesis
that's inside the term, which may not be what you want...

Yours,

Tom
http://generatingparserswithjavacc.com/


---------------------------------------------------------------------
To unsubscribe, e-mail: users-unsubscribe@javacc.dev.java.net
For additional commands, e-mail: users-help@javacc.dev.java.net

