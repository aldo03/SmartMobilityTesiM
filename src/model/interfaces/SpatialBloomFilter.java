package model.interfaces;

import java.io.Serializable;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface SpatialBloomFilter extends Library {
	SpatialBloomFilter INSTANCE = (SpatialBloomFilter) Native.loadLibrary(
            (Platform.isWindows() ? "sbfnewp.dll" : "simpleDLLLinuxPort"), SpatialBloomFilter.class);
	
	// Prints the filter and related statistics to the standart output
	// mode: 0    prints SBF stats only
	// mode: 1    prints SBF information and the full SBF content
    void PrintFilter(int mode);
    
    // Creates a SBF
 	// Arguments:
 	// bit_mapping    actual size of the filter (as in number of cells): for
 	//                instance, bit_mapping = 10 states that the SBF will be
 	//                composed by 2^10 cells. As such, the size can only be
 	//                defined as a power of 2. This value is bounded by the
 	//                MAX_BIT_MAPPING constant.
 	// HASH_family    specifies the hash function to be used. Currently available:
 	//                1: SHA1
 	//                4: MD4
 	//                5: MD5
 	// HASH_number    number of digests to be produced (by running the hash function
 	//                specified above using different salts) in the insertion and
 	//                check phases.
 	// AREA_number    number of areas over which to build the filter.
 	// salt_path      path to the file where to read from/write to the hash salts
 	//                (which are used to produce different digests).
 	//                If the file exists, reads one salt per line.
 	//                If the file doesn't exist, the salts are randomly generated
 	//                during the filter creation phase
    void CreateSBF(int bit_mapping, int HASH_family, int HASH_number, int AREA_number, String salt_path);
    
    // Prints the filter and related statistics onto a CSV file (path)
    // mode: 1    writes SBF metadata (CSV: key;value)
    // mode: 0    writes SBF cells (CSV: value)
    void SaveToDisk(String path, int mode);
    
	// Maps a single element (passed as a String) to the SBF. For each hash
	// function, internal method SetCell is called, passing elements coupled with
	// the area labels. The elements MUST be passed following the ascending-order
	// of area labels. If this is not the case, the self-collision calculation (done
	// by SetCell) will likely be wrong.
	// String string     element to be mapped
	// int size         length of the element
	// int area         the area label
    void Insert(String string, int size, int area);
    
    // Inserts a single area element into the SBF.
    // int index        index of the area
    // int area         the area label
    void InsertArea(int index, int area);
    
    // Verifies weather the input element belongs to one of the mapped sets.
    // Returns the area label (i.e. the identifier of the set) if the element
    // belongs to a set, 0 otherwise.
    // String string     the element to be verified
    // int size         length of the element
	int Check(String string, int size);
}
