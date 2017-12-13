/*
 * citygml4j - The Open Source Java API for CityGML
 * https://github.com/citygml4j
 * 
 * Copyright 2013-2017 Claus Nagel <claus.nagel@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cityjson.citygml2cityjson;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.cityjson.CityJSONBuilder;
import org.citygml4j.builder.cityjson.json.io.writer.CityJSONOutputFactory;
import org.citygml4j.builder.cityjson.json.io.writer.CityJSONWriter;
import org.citygml4j.builder.jaxb.CityGMLBuilder;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReader;

import cityjson.citygml2cityjson.util.SimpleTextureFileHandler;

public class cg2cj {

	public static void main(String[] args) throws Exception {
		final SimpleDateFormat df = new SimpleDateFormat("[HH:mm:ss] "); 
		
		Path ifile = Paths.get("/Users/hugo/temp/datacj/LOD3_Building_v200.gml");
		Path ofile = Paths.get("/Users/hugo/temp/datacj/json/LOD3_Building_v200.json");
		
		System.out.println(df.format(new Date()) + "setting up citygml4j context and CityGML builder");
		CityGMLContext ctx = CityGMLContext.getInstance();
		CityGMLBuilder builder = ctx.createCityGMLBuilder();
		
		// reading CityGML dataset
		System.out.println(df.format(new Date()) + "reading file into main memory");
		CityGMLInputFactory in = builder.createCityGMLInputFactory();
		CityGMLReader reader = in.createCityGMLReader(new File(ifile.toString()));
		
		// we know that the root element is a <core:CityModel> so we use a shortcut here
		CityModel cityModel = (CityModel)reader.nextFeature();
		reader.close();
		
		// create a CityJSON builder 
		System.out.println(df.format(new Date()) + "creating CityJSON builder");
		CityJSONBuilder jsonBuilder = ctx.createCityJSONBuilder();
		
		// create a CityJSON output factory
		System.out.println(df.format(new Date()) + "writing citygml4j object tree as CityJSON file");
		CityJSONOutputFactory out = jsonBuilder.createCityJSONOutputFactory();		

		/**
		 * we can use different helpers on the CityJSON output factory such as builders
		 * for the "vertices" and "vertices-texture" arrays. Especially when converting
		 * an existing CityGML dataset, it is recommended that you provide your own 
		 * texture file handler.
		 * A texture file handler is invoked for every texture image found in the CityGML
		 * data. Its task is to generate a filename for the "image" property of a CityJSON
		 * texture object and to possibly copy the image file to the "appearances" folder.
		 * If you do not provide your own texture file handler, a default one will be used
		 * which only adapts the file name for the CityJSON file. 
		 */
		
		SimpleTextureFileHandler textureFileHandler = new SimpleTextureFileHandler(Paths.get(ifile.getParent().toString()), Paths.get(ofile.getParent().toString() + "/appearances"));
		out.setProperty(CityJSONOutputFactory.TEXTURE_FILE_HANDLER, textureFileHandler);
		
		// create a simple CityJSON writer
		CityJSONWriter writer = out.createCityJSONWriter(new File(ofile.toString()));
		writer.write(cityModel);
		writer.close();
		
		System.out.println(df.format(new Date()) + "CityJSON file  written");
		System.out.println(df.format(new Date()) + "application successfully finished");
	}

}
