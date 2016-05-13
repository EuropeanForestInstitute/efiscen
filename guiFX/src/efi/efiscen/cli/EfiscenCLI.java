/* 
 * Copyright (C) 2016 European Forest Institute
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package efi.efiscen.cli;

/**
 * Command line interface for efiscen. Parameters:
 * <p>1. steps - Number of steps to run the simulation.
 * <br>2. thinning - Intensity of the thinning applied during the simulation.
 * <br>3. felling - Intensity of the felling applied during the simulation.
 * <br>4. experiment - File path to an experiment file. File name must be
 * included.
 * <br>5. scaling - Scaling factor that is applied globally to areas of all the
 * matrices.
 * <br>6. scenario - File path to a scenario file. The file name must be
 * included and the name must end with “.scn”.
 * <br>7. outputfile - File path to where the output files will be saved. Path must
 * include a file name.
 * <br>8. databaseaddress - Address to a database where outputs will be saved.
 * <br>9. username - User name used to log into the database.
 * <br>10. password - Password used to log into the database.
 * <br>11. sid - Session id that will be present in all data entries saved into
 * a database.
 * <br>12. ciso - ISO country-code is used to identify that the output data
 * concerns a certain country.
 * <br>13. selected - File path to a text file containing definitions about
 * which outputs to save.
 * <br>The Path must include the file name and the name must end with “.txt”.
 * <br>14. pid - Project id.
 * <p>
 * When running EFISCEN and using database, parameters 6, 7, 13 are optional.
 * <br>When running EFISCEN and saving outputs as files, parameters 1-5 and 7
 * <br>are required and 6, 13 are optional.
 *
 * 
 */
public class EfiscenCLI {

    /**
     * Main method to run efiscen from command line. See parameters from description.
     * @param args 
     */
    public static void main(String[] args){
        Application app = new Application(args);
        app.run();
        return;
    }
}