import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * NMEAParser handles handles parsing a subset of the NMEA 0183 protocol.
 * 
 * @author Mason Foster
 */
public class NMEAParser {
	private Pattern gll;
	private Matcher currMatcher;
	private Scanner scan;
	
	public NMEAParser(){
		this.gll = Pattern.compile("\\$(GP|LC|IT|IN|EC|CD|GL|GN)GLL,([0-9]){4}\\.([0-9]){2}(([0-9]){2})?,(N|S),([0-9]){5}\\.([0-9]){2}(([0-9]){2})?,(E|W),(((0|1)[0-9])|2[0-3])[0-5][0-9][0-5][0-9]\\.[0-9][0-9],(A|V),([0-9a-zA-Z],)?\\*[0-9a-fA-F][0-9a-fA-F]\r\n");
	}
	
	public GPSLocation parseSentence(String sentence){
		GPSLocation gpsloc = null;
		
		// Parse a GLL Sentence
		this.currMatcher = this.gll.matcher(sentence);
		if(currMatcher.find()){
			GPSCoordinate latitude,longitude;
			String faaTemp, checksum;
			this.scan = new Scanner(sentence);
			this.scan.useDelimiter(",");
			// Skip "$--GLL"
			scan.next(); 
			// Set Latitude value
			latitude = new GPSCoordinate(CoordinateType.Latitude, 
					Hemisphere.INVALID, Double.parseDouble(scan.next()));
			// Set Latitude Hemisphere
			latitude.setHemisphere((scan.next().equals("N")) ? 
					Hemisphere.North : Hemisphere.South);
			// Set Longitude value
			longitude = new GPSCoordinate(CoordinateType.Longitude, 
					Hemisphere.INVALID, Double.parseDouble(scan.next()));
			// Set Longitude Hemisphere
			longitude.setHemisphere((scan.next().equals("E")) ? 
					Hemisphere.East : Hemisphere.West);
			// Skip Data Validity
			scan.next();
			// Skip Time
			scan.next();
			// Check if FAA Mode exists, else Set Checksum
			faaTemp = scan.next();
			if(faaTemp.contains("*")){
				checksum = faaTemp;
			}
			else{
				checksum = scan.next();
			}
			gpsloc = new GPSLocation(latitude, longitude);
			gpsloc.setCheckSum(checksum); // Store Checksum
			gpsloc.setSentence(sentence); // Store NMEA Sentence	
		}
		return gpsloc;
	}
}

/*
------------------------------------------------------------------------------
       1       2 3        4 5         6 7   8   
       |       | |        | |         | |   |   
$--GLL,llll.ll,a,yyyyy.yy,a,hhmmss.ss,a,m,*hh<CR><LF>
------------------------------------------------------------------------------

Field Number: 

1. Latitude
2. N or S (North or South)
3. Longitude
4. E or W (East or West)
5. Universal Time Coordinated (UTC)
6. Status A - Data Valid, V - Data Invalid
7. FAA mode indicator (NMEA 2.3 and later)
8. Checksum

GP | LC | IT | IN | EC | CD | GL | GN
*/
