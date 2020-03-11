/**
 * Copyright 2017-2019 Riverside Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.phenix.pct;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import eu.rssw.pct.RCodeInfo.InvalidRCodeException;

/**
 * Class for check RCode Validity
 * 
 */
public class CheckRCode extends PCT {

    private int nbError = 0;
    private int nbFiles = 0;
    private List<FileSet> filesets = new ArrayList<>();

    public CheckRCode() {
        super();
    }

    /**
     * Adds a set of files to archive.
     * 
     * @param set FileSet
     */
    public void addFileset(FileSet set) {
        filesets.add(set);
    }

    @Override
    public void execute() {

        log("CheckRCode - Vérification des rcodes");

        // There must be at least one fileset
        if (filesets.isEmpty()) {
            throw new BuildException(Messages.getString("OpenEdgeClassDocumentation.1"));
        }

        for (FileSet fs : filesets) {
            verifRCode(fs);
        }

        log(MessageFormat.format("{0} fichier(s) testé(s)", nbFiles));
        
        if (nbError > 0) {
            log(MessageFormat.format("{0} fichier(s) invalide(s)", nbError));
            throw new BuildException("Some RCode are not valid");
        }

    }

    /**
     * 
     * @param fs FileSet to be written
     * @throws BuildException
     */
    private void verifRCode(FileSet fs) {
        
        for (String str : fs.getDirectoryScanner(getProject()).getIncludedFiles()) {
            // check not including the pl itself in the pl
            File resourceAsFile = new File(fs.getDir(getProject()), str);
            
            if (resourceAsFile.getAbsolutePath().toLowerCase().endsWith(".r")) {
                nbFiles ++;
                try {
                    FileInputStream input = new FileInputStream(resourceAsFile);
                    eu.rssw.pct.RCodeInfo rci = new eu.rssw.pct.RCodeInfo(input);
                    log(resourceAsFile + " -> OK", Project.MSG_VERBOSE);
                } catch (Exception caught) {
                    log(resourceAsFile + " -> Erreur", Project.MSG_ERR);
                    nbError++;
                }

            }
        }

    }

}
