package org.ei.tags;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ei.util.*;

 public class TagFilter
 {

	protected static LinkedHashMap badInput = new LinkedHashMap();
	/* Bad words taken from Greasemonkey Script http://userscripts.org/scripts/show/4175 */
	protected static Pattern badwords = Pattern.compile("\\b(wtf|wop|whore|whoar|wetback|wank|vagina|twaty|twat|titty|titties|tits|testicles|teets|spunk|spic|snatch|smut|sluts|slut|sleaze|slag|shiz|shitty|shittings|shitting|shitters|shitter|shitted|shits|shitings|shiting|shitfull|shited|shit|shemale|sheister|sh!t|scrotum|screw|schlong|retard|qweef|queer|queef|pussys|pussy|pussies|pusse|punk|prostitute|pricks|prick|pr0n|pornos|pornography|porno|porn|pissoff|pissing|pissin|pisses|pissers|pisser|pissed|piss|pimp|phuq|phuks|phukking|phukked|phuking|phuked|phuk|phuck|phonesex|penis|pecker|orgasms|orgasm|orgasims|orgasim|niggers|nigger|nigga|nerd|muff|mound|motherfucks|motherfuckings|motherfucking|motherfuckin|motherfuckers|motherfucker|motherfucked|motherfuck|mothafucks|mothafuckings|mothafucking|mothafuckin|mothafuckers|mothafucker|mothafucked|mothafuckaz|mothafuckas|mothafucka|mothafuck|mick|merde|masturbate|lusting|lust|loser|lesbo|lesbian|kunilingus|kums|kumming|kummer|kum|kuksuger|kuk|kraut|kondums|kondum|kock|knob|kike|kawk|jizz|jizm|jiz|jism|jesus h christ|jesus fucking christ|jerk-off|jerk|jap|jackoff|jacking off|jackass|jack-off|jack off|hussy|hotsex|horny|horniest|hore|hooker|honkey|homo|hoer|hell|hardcoresex|hard on|h4x0r|h0r|guinne|gook|gonads|goddamn|gazongers|gaysex|gay|gangbangs|gangbanged|gangbang|fux\0r|furburger|fuks|fuk|fucks|fuckme|fuckings|fucking|fuckin|fuckers|fucker|fucked|fuck|fu|foreskin|fistfucks|fistfuckings|fistfucking|fistfuckers|fistfucker|fistfucked|fistfuck|fingerfucks|fingerfucking|fingerfuckers|fingerfucker|fingerfucked|fingerfuck|fellatio|felatio|feg|feces|fcuk|fatso|fatass|farty|farts|fartings|farting|farted|fart|fags|fagots|fagot|faggs|faggot|faggit|fagging|fagget|fag|ejaculation|ejaculatings|ejaculating|ejaculates|ejaculated|ejaculate|dyke|dumbass|douche bag|dong|dipshit|dinks|dink|dildos|dildo|dike|dick|damn|damn|cyberfucking|cyberfuckers|cyberfucker|cyberfucked|cyberfuck|cyberfuc|cunts|cuntlicking|cuntlicker|cuntlick|cunt|cunnilingus|cunillingus|cunilingus|cumshot|cums|cumming|cummer|cum|crap|cooter|cocksucks|cocksucking|cocksucker|cocksucked|cocksuck|cocks|cock|cobia|clits|clit|clam|circle jerk|chink|cawk|buttpicker|butthole|butthead|buttfucker|buttfuck|buttface|butt hair|butt fucker|butt breath|butt|butch|bung hole|bum|bullshit|bull shit|bucket cunt|browntown|browneye|brown eye|boner|bonehead|blowjobs|blowjob|blow job|bitching|bitchin|bitches|bitchers|bitcher|bitch|bestiality|bestial|belly whacker|beaver|beastility|beastiality|beastial|bastard|balls|asswipe|asskisser|assholes|asshole|asses|ass lick|ass)\\b", Pattern.CASE_INSENSITIVE);
	static
	{
	    badInput.put(badwords, "");
    }

	public String[] filterParameter(String[] paramValues)
	{
		if(paramValues != null)
		{
			for(int i = 0; i < paramValues.length; i++)
			{
				String filteredValue = filterParameter(paramValues[i]);
				if(filteredValue != null)
				{
					paramValues[i] = filteredValue;
				}
			}
		}
		return paramValues;
	}

	public String filterParameter(String paramValue)
	{
		paramValue = AlphaNumericFilter.alphaNumeric(paramValue);

		if(paramValue != null)
		{
			Iterator itrPatterns = badInput.keySet().iterator();
			while(itrPatterns.hasNext())
			{
				Pattern p = (Pattern) itrPatterns.next();
				Matcher m = p.matcher(paramValue);
				paramValue = m.replaceAll((String) badInput.get(p));
			}
		}
		return paramValue;
	}

}
