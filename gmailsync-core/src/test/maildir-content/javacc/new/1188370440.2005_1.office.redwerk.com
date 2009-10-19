X-Spam-Checker-Version: SpamAssassin 3.2.1 (2007-05-02) on office.redwerk.com
X-Spam-Level: 
X-Spam-Status: No, score=0.0 required=5.0 tests=none autolearn=failed
	version=3.2.1
Received: from [::1] (helo=office.redwerk.com)
	by office.redwerk.com with esmtp (Exim 4.67 (FreeBSD))
	(envelope-from <users-return-2028-bofh=redwerk.com@javacc.dev.java.net>)
	id 1IQHP5-0000La-Rm
	for bofh@localhost; Wed, 29 Aug 2007 09:51:52 +0300
X-Original-To: bofh@redwerk.com
Delivered-To: bofh@redwerk.com
Received: from redwerk.com [69.61.72.191]
	by office.redwerk.com with IMAP (fetchmail-6.3.8)
	for <bofh@localhost> (single-drop); Wed, 29 Aug 2007 09:51:51 +0300 (EEST)
Received: from dev.java.net (s015.sjc.collab.net [204.16.104.198])
	by redwerk.com (Postfix) with SMTP id EE2E7119025
	for <bofh@redwerk.com>; Wed, 29 Aug 2007 04:31:29 +0200 (CEST)
Received: (qmail 13885 invoked by uid 5000); 29 Aug 2007 02:31:21 -0000
Mailing-List: contact users-help@javacc.dev.java.net; run by ezmlm
Precedence: bulk
X-No-Archive: yes
list-help: <mailto:users-help@javacc.dev.java.net>
list-unsubscribe: <mailto:users-unsubscribe@javacc.dev.java.net>
list-post: <mailto:users@javacc.dev.java.net>
Reply-To: users@javacc.dev.java.net
Delivered-To: mailing list users@javacc.dev.java.net
Received: (qmail 13870 invoked from network); 29 Aug 2007 02:31:21 -0000
X-IronPort-Anti-Spam-Filtered: true
X-IronPort-Anti-Spam-Result: AgAAAAd31EbRVZKymGdsb2JhbACNfgEBAQEHBAQRGA
X-IronPort-AV: E=Sophos;i="4.19,319,1183359600"; 
   d="scan'208";a="65727431"
X-IRONPORT: SCANNED
DKIM-Signature: a=rsa-sha1; c=relaxed/relaxed;
        d=gmail.com; s=beta;
        h=domainkey-signature:received:received:message-id:date:from:to:subject:in-reply-to:mime-version:content-type:content-transfer-encoding:content-disposition:references;
        b=WcBLT3eiVfiQUpzg3ZVsHxhl8Pxol6IDt9CnDLEm87Ks/YScr6S+875W45NOvKKuDcnNKr7QXm7YsaWV2QIsbRZxry0HKYzx79KU7mN1+4L/NpLC6exSLHXKKYGiRI1WWKwUl1b4yb65d/+Lk4/72g64e1E8PB9/XBtHwSQWM3I=
DomainKey-Signature: a=rsa-sha1; c=nofws;
        d=gmail.com; s=beta;
        h=received:message-id:date:from:to:subject:in-reply-to:mime-version:content-type:content-transfer-encoding:content-disposition:references;
        b=O6/vBM679AIlZfZeuz9rm/nwK9WuhyxBXWFzkaXlwhA1rE4ndGR7Z+xidqyLjVkiUCvJREN96StXtJPcZkZ4DI4dl9i1odMnm8/LLhmQr62cVyBnUFejc/o6QCfG+WuiMYcg4x/Rr87zpjzCE3FxjhVoWNJeQt0Fv6Ee7ry3ad4=
Message-ID: <839ba01c0708281931r5205f31fnec7139404c4cd903@mail.gmail.com>
Date: Wed, 29 Aug 2007 10:31:20 +0800
From: "Cedric Ho" <cedric.ho@gmail.com>
To: users@javacc.dev.java.net
In-Reply-To: <1188306521.21853.12.camel@bugs.hal>
MIME-Version: 1.0
Content-Type: text/plain; charset=UTF-8
Content-Transfer-Encoding: 7bit
Content-Disposition: inline
References: <839ba01c0708262138i4232c1c3w88e9355e29b7fe34@mail.gmail.com>
	 <839ba01c0708270126u206a1b31m2898a648bf3bf60c@mail.gmail.com>
	 <d15ae0610708270545o32520028y999ed07fbd2f612b@mail.gmail.com>
	 <839ba01c0708271957r4d94c0bvb6c7fae03c786c44@mail.gmail.com>
	 <d15ae0610708280100u7b81d85eo3b95b23d7f0f026b@mail.gmail.com>
	 <839ba01c0708280320n3b9b6c44l75d823b4c9eb9d69@mail.gmail.com>
	 <1188306521.21853.12.camel@bugs.hal>
Subject: Re: [JavaCC] Re: Questions on matching nested open, close brackets

Hi, Chris, Tom,

I just fixed the error after some manual tracing of the execution in
eclipse's debugger. It seems that although the token returned from the
token manager is ")", the token.type is incorrect (a 9 instead of a 11
that jj_consume_token(11) expect). That's why the error message occur:

ParseException: Encountered ")" at line 1, column 9.
Was expecting one of:
   "(" ...
   <TERM> ...
   ")" ...

I fixed it by assigning names to the tokens "(" and ")":
<IN_TERM> TOKEN : {
	<OPEN: "("> {tokenDepth += 1; }
|
	<CLOSE: ")"> { tokenDepth -= 1;
		SwitchTo(tokenDepth==0 ? DEFAULT : IN_TERM);
	}
|
	<TERM: ~["\t", "\n", "\r", "(", ")"] (~["\t", "\n", "\r", "(", ")"])*>
}

I still do not understand why I need to give them names to make their
token.type correct. But anyway, thanks a lot for helping me through
this.

Regards,
Cedric






On 8/28/07, Tom Copeland <tom@infoether.com> wrote:
> On Tue, 2007-08-28 at 18:20 +0800, Cedric Ho wrote:
> > from the "JavaCC FAQ 3.17 How do I tokenize nested comments?"
> > it seems that it is what I would needed. However when I try to follow
> > its example, I encountered the following error during parsing:
> >
> > Exception in thread "main"
> > wisers.wisengp.parser.normal.ParseException: Encountered ")" at line
> > 1, column 9.
> > Was expecting one of:
> >     "(" ...
> >     <TERM> ...
> >     ")" ...
> >
> > I am pretty confused with the error message. Since I haven't change
> > anything else yet. I just followed the example and added a Lexcial
> > State IN_TERM.
>
> I think you're on the right path.  One thing I usually do when I start
> hitting errors like this is ensure that the scanner is tokenizing
> everything like I expect it to.  You can do this by just running your
> TokenManager, e.g.:
>
> ============================
> $ cat terms.jj
> options {
>   BUILD_PARSER=false;
> }
> PARSER_BEGIN(Terms)
> public class Terms {}
> PARSER_END(Terms)
> TOKEN_MGR_DECLS : {
>   public static void main(String[] args) {
>     String data = "AND(OR((term(with)bracket) (term with space))
> ((another)term with space))";
>     TermsTokenManager mgr = new TermsTokenManager(new
> SimpleCharStream(new java.io.StringReader(data)));
>     Token t;
>     while ((t = mgr.getNextToken()) != null) {
>       debugStream.println("Token: " + t.image);
>     }
>   }
>   private static int tokenDepth = 0;
> }
>
> TOKEN : {
>   <AND: "AND("> | <OR: "OR("> | <AND_NOT: "AND_NOT(">
>   | "(" {tokenDepth = 1;} : IN_TERM
> }
>
> <IN_TERM> TOKEN : {
>   "(" {tokenDepth += 1; }
>   | ")" { tokenDepth -= 1; SwitchTo(tokenDepth==0 ? DEFAULT :
> IN_TERM); }
>   | <TERM: ~["\t", "\n", "\r", "(", ")", " "] (~["\t", "\n", "\r", "(",
> ")"])*>
> }
> $ javacc terms.jj && javac *.java && java TermsTokenManager
> Java Compiler Compiler Version 4.0 (Parser Generator)
> (type "javacc" with no arguments for help)
> Reading from file terms.jj . . .
> Parser generated successfully.
> Token: AND(
> Token: OR(
> Token: (
> Token: term
> Token: (
> Token: with
> Token: )
> Token: bracket
> Token: )
> Exception in thread "main" TokenMgrError: Lexical error at line 1,
> column 27.  Encountered: " " (32), after : ""
>         at TermsTokenManager.getNextToken(TermsTokenManager.java:453)
>         at TermsTokenManager.main(TermsTokenManager.java:9)
> ============================
>
> You can see here that a token is being created for the left parenthesis
> that's inside the term, which may not be what you want...
>
> Yours,
>
> Tom
> http://generatingparserswithjavacc.com/
>
>
> ---------------------------------------------------------------------
> To unsubscribe, e-mail: users-unsubscribe@javacc.dev.java.net
> For additional commands, e-mail: users-help@javacc.dev.java.net
>
>

---------------------------------------------------------------------
To unsubscribe, e-mail: users-unsubscribe@javacc.dev.java.net
For additional commands, e-mail: users-help@javacc.dev.java.net

