/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.kenyaemr.fragment.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Handles AJAX patient searches
 */
public class PatientSearchFragmentController {
	
	public List<SimpleObject> search(@RequestParam(value="q", required=false) String query,
	                     @RequestParam(value="age", required=false) Integer age,
	                     @RequestParam(value="ageWindow", defaultValue="5") int ageWindow,
	                     UiUtils ui) {
		if (StringUtils.isBlank(query))
			return null;
		
		List<Patient> ret = Context.getPatientService().getPatients(query);
		if (age != null) {
			List<Patient> similar = new ArrayList<Patient>();
			for (Patient p : ret) {
				if (Math.abs(p.getAge() - age) <= ageWindow)
					similar.add(p);
			}
			ret = similar;
		}
		return simplePatientList(ui, ret);
	}
	
	public List<SimpleObject> withActiveVisits(UiUtils ui) {
		List<Visit> activeVisits = Context.getVisitService().getVisits(null, null, null, null, null, null, null, null, null, false, false);

		List<Patient> ret = new ArrayList<Patient>();
		for (Visit v : activeVisits) {
			if (!ret.contains(v.getPatient()))
				ret.add(v.getPatient());
		}
		return simplePatientList(ui, ret);
	}

	/**
     * Simplifies a list of patients so it can be sent to the client via json
     * 
     * @param ui
     * @param pts
     * @return
     */
    private List<SimpleObject> simplePatientList(UiUtils ui, List<Patient> pts) {
    	return SimpleObject.fromCollection(pts, ui, "patientId", "personName", "age", "gender", "activeIdentifiers.identifierType", "activeIdentifiers.identifier");
    }

}