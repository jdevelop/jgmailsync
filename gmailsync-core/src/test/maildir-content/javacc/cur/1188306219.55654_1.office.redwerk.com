X-Spam-Checker-Version: SpamAssassin 3.2.1 (2007-05-02) on office.redwerk.com
X-Spam-Level: 
X-Spam-Status: No, score=0.0 required=5.0 tests=none autolearn=failed
	version=3.2.1
Received: from [::1] (helo=office.redwerk.com)
	by office.redwerk.com with esmtp (Exim 4.67 (FreeBSD))
	(envelope-from <users-return-2026-bofh=redwerk.com@javacc.dev.java.net>)
	id 1IQ0jK-000ETZ-HM
	for bofh@localhost; Tue, 28 Aug 2007 16:03:39 +0300
X-Original-To: bofh@redwerk.com
Delivered-To: bofh@redwerk.com
Received: from redwerk.com [69.61.72.191]
	by office.redwerk.com with IMAP (fetchmail-6.3.8)
	for <bofh@localhost> (single-drop); Tue, 28 Aug 2007 16:03:38 +0300 (EEST)
Received: from dev.java.net (s015.sjc.collab.net [204.16.104.198])
	by redwerk.com (Postfix) with SMTP id CE430119021
	for <bofh@redwerk.com>; Tue, 28 Aug 2007 15:02:56 +0200 (CEST)
Received: (qmail 14683 invoked by uid 5000); 28 Aug 2007 13:02:39 -0000
Mailing-List: contact users-help@javacc.dev.java.net; run by ezmlm
Precedence: bulk
X-No-Archive: yes
list-help: <mailto:users-help@javacc.dev.java.net>
list-unsubscribe: <mailto:users-unsubscribe@javacc.dev.java.net>
list-post: <mailto:users@javacc.dev.java.net>
Reply-To: users@javacc.dev.java.net
Delivered-To: mailing list users@javacc.dev.java.net
Received: (qmail 14660 invoked from network); 28 Aug 2007 13:02:38 -0000
X-IronPort-Anti-Spam-Filtered: true
X-IronPort-Anti-Spam-Result: AgAAAC+500bRVZKwkmdsb2JhbACNfQEBAgcEBBEW
X-IronPort-AV: E=Sophos;i="4.19,316,1183359600"; 
   d="scan'208";a="65675692"
X-IRONPORT: SCANNED
DKIM-Signature: a=rsa-sha1; c=relaxed/relaxed;
        d=gmail.com; s=beta;
        h=domainkey-signature:received:received:message-id:date:from:to:subject:in-reply-to:mime-version:content-type:content-transfer-encoding:content-disposition:references;
        b=kmuKRsxZ/9vx4+6Cc/bdCnVVnXrtK9a52WDd9+1PW8p3QBZJMuN3eB4FsTqGjmTfVzBZyagRzMMzeO0ctMb5VHN6V7q3WyckeRq1R0xGYVMC2WX+VgaS2fGflyNc1kvuTYKCMHICOl4VElskbFMsFxG+yCIdmil/PFWUT0/Reo8=
DomainKey-Signature: a=rsa-sha1; c=nofws;
        d=gmail.com; s=beta;
        h=received:message-id:date:from:to:subject:in-reply-to:mime-version:content-type:content-transfer-encoding:content-disposition:references;
        b=AVpY8DwjrJR+RNc+3x4DB5V/uzfyzdn89yotAc9Fm0ID0vk8K9LCfmabyOMXHPMBVgQYDu79CwjxUrz0yHcQ8zEemfcNgf6RDegBM6Z2wTwQBGJA7IiOvJNy0aNjOEm35UA4NjC+U6apDjSLym+VnkEO7K/1dD9fdC11HCguzwU=
Message-ID: <d15ae0610708280602g9c34e4bw9acf63c7d36415c9@mail.gmail.com>
Date: Tue, 28 Aug 2007 21:02:36 +0800
From: "J.Chris Findlay" <j.chris.findlay@gmail.com>
To: users@javacc.dev.java.net
In-Reply-To: <839ba01c0708280320n3b9b6c44l75d823b4c9eb9d69@mail.gmail.com>
MIME-Version: 1.0
Content-Type: text/plain; charset=ISO-8859-1
Content-Transfer-Encoding: 7bit
Content-Disposition: inline
References: <839ba01c0708262138i4232c1c3w88e9355e29b7fe34@mail.gmail.com>
	 <839ba01c0708270126u206a1b31m2898a648bf3bf60c@mail.gmail.com>
	 <d15ae0610708270545o32520028y999ed07fbd2f612b@mail.gmail.com>
	 <839ba01c0708271957r4d94c0bvb6c7fae03c786c44@mail.gmail.com>
	 <d15ae0610708280100u7b81d85eo3b95b23d7f0f026b@mail.gmail.com>
	 <839ba01c0708280320n3b9b6c44l75d823b4c9eb9d69@mail.gmail.com>
Subject: Re: [JavaCC] Re: Questions on matching nested open, close brackets

Looks ok so far - try enabling some of the debugging options to see
what's going on...

On 28/08/07, Cedric Ho <cedric.ho@gmail.com> wrote:
> Hi, sorry to bother again,
>
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
> State IN_TERM. Have I done anything stupid?
>
> TOKEN_MGR_DECLS : {
>     int tokenDepth;
> }
>
> TOKEN : {
>         <AND: "AND("> | <OR: "OR("> | <AND_NOT: "AND_NOT(">
> |
>         "(" {tokenDepth = 1;} : IN_TERM
> }
>
> <IN_TERM> TOKEN : {
>         "(" {tokenDepth += 1; }
> |
>         ")" { tokenDepth -= 1;
>                 SwitchTo(tokenDepth==0 ? DEFAULT : IN_TERM);
>         }
> |
>         <TERM: ~["\t", "\n", "\r", "(", ")", " "] (~["\t", "\n", "\r", "(", ")"])*>
> }
>
>
>
> On 8/28/07, J.Chris Findlay <j.chris.findlay@gmail.com> wrote:
> > Probably easiest to switch lexical states when you encounter the start
> > of a term, and return to the default when it hits the matching closing
> > ).
> > In this new state, the term definition is made, and contains space.
> > In the default state, space is treated as it is currently.
> >
> > On 28/08/07, Cedric Ho <cedric.ho@gmail.com> wrote:
> > > Thanks, I can now match the terms with brackets. However now I get the
> > > problem with the spaces within terms. In particular, I cannot match
> > > the following term:
> > >
> > > ((ab) cd)
> > >
> > > If I allow the " " character in the <TERM> definition. It will match
> > > outside of the boundary of the term.
> > >
> > > Also, there are two warnings complaining about Choice conflict in (...)*
> > >
> > > The following is my parser's code
> > >
> > > /* Token Definition */
> > > SKIP : {
> > >        "\t" | "\n" | "\r"
> > > }
> > >
> > > TOKEN : {
> > >        <AND: "AND("> | <OR: "OR("> | <AND_NOT: "AND_NOT(">
> > > |
> > >        <TERM: ~["\t", "\n", "\r", "(", ")", " "] (~["\t", "\n", "\r", "(", ")"])*>
> > > }
> > >
> > > String parseIt() :
> > > { String s; }
> > > {
> > >        s = expression()
> > >        { return s; }
> > > }
> > >
> > > String expression() :
> > > { Token t; String s; }
> > > {
> > >        <AND> s = expressions("AND") ")"
> > >        { return "(" + s + ")"; }
> > > |
> > >        <OR> s = expressions("OR") ")"
> > >        { return "(" + s + ")"; }
> > > |
> > >        <AND_NOT> s = expressions("AND NOT") ")"
> > >        { return "(" + s + ")"; }
> > > |
> > >        "(" s = term() ")"
> > >        {
> > >                System.out.println(s);
> > >                return "\"" + s + "\"";
> > >        }
> > > }
> > >
> > > String expressions(String op) :
> > > { String result = ""; String s; }
> > > {
> > >        result = expression()
> > >        (" " s = expression()
> > >                { result += " " + op + " " + s; }
> > >        )*
> > >        { return result; }
> > > }
> > >
> > > String term() :
> > > {
> > >        Token t;
> > >        String result;
> > >        String s;
> > > }
> > > {
> > >        result = simpleTerm()
> > >        (
> > >                s = term()
> > >                { result += s; }
> > >        )*
> > >        { return result; }
> > > |
> > >        ("(" result = term() ")"
> > >                { result = "(" + result + ")"; }
> > >        )
> > >        (s = term()
> > >                { result += s; }
> > >        )*
> > >        { return result; }
> > > }
> > >
> > > String simpleTerm() :
> > > { Token t; }
> > > {
> > >        t = <TERM>
> > >        { return t.image; }
> > > }
> > >
> > >
> > >
> > > On 8/27/07, J.Chris Findlay <j.chris.findlay@gmail.com> wrote:
> > > > It appears the definition for the AND and OR function are that they
> > > > are immediately followed by a pair of ()'s, the contents of which is a
> > > > sequence of terms or functions, and that a term is always enclosed in
> > > > a pair of ()'s.
> > > > Thus, on encountering a keyword, you know what to expect next, and on
> > > > encountering a ( otherwise means the start of a term, which may
> > > > essentially recursively contain terms (so that the ()'s match).
> > > >
> > > > On 27/08/07, Cedric Ho <cedric.ho@gmail.com> wrote:
> > > > > An example:
> > > > >
> > > > > AND(OR((term(with)bracket) (term with space)) ((another)term with space))
> > > > >
> > > > > would yield the following 3 terms:
> > > > > term(with)bracket
> > > > > term with space
> > > > > (another)term with space
> > > > >
> > > > > From the FAQ. it seems I may need to use SwitchTo to do this. But I am
> > > > > not sure how.
> > > > >
> > > > > Regards,
> > > > > Cedric
> > > > >
> > > > >
> > > > > On 8/27/07, Cedric Ho <cedric.ho@gmail.com> wrote:
> > > > > > Hi all,
> > > > > >
> > > > > > I would like to parse the following string and extract all the <TERM>s:
> > > > > >
> > > > > > AND((<TERM>) (<TERM>) OR((<TERM>) (<TERM>) (<TERM>)))
> > > > > >
> > > > > > where AND, OR are keywords and <TERM> consists of any characters
> > > > > > including space, "(" and ")". If "(" or ")" appear in a <TERM> they
> > > > > > will be matching each other.
> > > > > >
> > > > > > I've been trying for quite a while and cannot seems to write a correct
> > > > > > parser successfully.
> > > > > >
> > > > > > Any help is appreciated.
> > > > > >
> > > > > > Regards,
> > > > > > Cedric
> > > > > >
> > > > >
> > > > > ---------------------------------------------------------------------
> > > > > To unsubscribe, e-mail: users-unsubscribe@javacc.dev.java.net
> > > > > For additional commands, e-mail: users-help@javacc.dev.java.net
> > > > >
> > > > >
> > > >
> > > >
> > > > --
> > > >  - J.Chris Findlay
> > > >    (c:
> > > >
> > > > ---------------------------------------------------------------------
> > > > To unsubscribe, e-mail: users-unsubscribe@javacc.dev.java.net
> > > > For additional commands, e-mail: users-help@javacc.dev.java.net
> > > >
> > > >
> > >
> > > Regards,
> > > Cedric
> > >
> > > ---------------------------------------------------------------------
> > > To unsubscribe, e-mail: users-unsubscribe@javacc.dev.java.net
> > > For additional commands, e-mail: users-help@javacc.dev.java.net
> > >
> > >
> >
> >
> > --
> >  - J.Chris Findlay
> >    (c:
> >
> > ---------------------------------------------------------------------
> > To unsubscribe, e-mail: users-unsubscribe@javacc.dev.java.net
> > For additional commands, e-mail: users-help@javacc.dev.java.net
> >
> >
>
>
> Regards,
> Cedric
>
> ---------------------------------------------------------------------
> To unsubscribe, e-mail: users-unsubscribe@javacc.dev.java.net
> For additional commands, e-mail: users-help@javacc.dev.java.net
>
>


-- 
 - J.Chris Findlay
   (c:

---------------------------------------------------------------------
To unsubscribe, e-mail: users-unsubscribe@javacc.dev.java.net
For additional commands, e-mail: users-help@javacc.dev.java.net

