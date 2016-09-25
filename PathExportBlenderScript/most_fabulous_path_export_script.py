######################## add-in info ######################

bl_info = {
    "name": "Export Animation Paths",
    "category": "Import-Export",
    "author": "Julian Ewers-Peters",
    "location": "File > Import-Export"
}

######################## imports ##########################

import bpy
import bpy.types
from bpy_extras.io_utils import ExportHelper
import bpy.props
from bpy.props import *
import os.path
import struct

######################## helper ###########################

class PathExportOptions():
    enable_dbg  = False
    write_bin   = True
    write_ascii = False
    export_tilt = False    

######################## main #############################

class PathExporter(bpy.types.Operator, ExportHelper):
    """Export Animation Paths"""
    bl_idname   = "export.export_paths"
    bl_label    = "Export Animation Paths"
    bl_options  = {'REGISTER', 'PRESET'}

    filename_ext = ".kbap"

    enable_dbg  = BoolProperty(name="Log Debug Info to Console", default=False)
    write_bin   = BoolProperty(name="Export Paths as Binary", default=True)
    write_ascii = BoolProperty(name="Export Paths as ASCII", default=False)
    export_tilt = BoolProperty(name="Export Tilt", default=False)    

    def execute(self, context):
        #get list of objects from scene
        object_list = list(bpy.data.objects)

        #get file path from UI
        filepath = self.filepath

        #export options
        export_options = PathExportOptions()
        export_options.write_bin   = self.write_bin
        export_options.write_ascii = self.write_ascii
        export_options.export_tilt = self.export_tilt
        export_options.enable_dbg  = self.enable_dbg

        #IMPORTANT NOTE: Blender Curve Paths MUST be in 'POLY' mode!
        for scene_obj in object_list:
            if scene_obj.type == 'CURVE' and scene_obj.name[:5] == 'kbap_':
                print("CURVE found for export: " + scene_obj.name)
                write(scene_obj, filepath, export_options)                
        
        if export_options.enable_dbg == True:
            print("Write Binary: " + str(export_options.write_bin))
            print("Write ASCII: "  + str(export_options.write_ascii))
            print("Export Tilt: "  + str(export_options.export_tilt))

        return {'FINISHED'}
        
def menu_func(self, context):
    self.layout.operator(PathExporter.bl_idname, text="Export Animation Paths (*.bin | *.txt)");

######################## file writer ######################

def write(scene_obj, filepath, export_options):
    filepath_bin = filepath[:-5] + '_' + scene_obj.name + '.bin'
    filepath_txt = filepath[:-5] + '_' + scene_obj.name + '.txt'

    #write Binary
    if export_options.write_bin == True:
        write_binary_file(scene_obj, filepath_bin, export_options)

    #write ASCII
    if export_options.write_ascii == True:
        write_ascii_file(scene_obj, filepath_txt, export_options)

######################## binary writer ####################

def write_binary_file(scene_obj, filepath_bin, export_options):
    outputfile_bin = open(filepath_bin, 'wb')

    try:
        for spline in scene_obj.data.splines:
            #only write POLY
            if spline.type != 'POLY':
                print("ERROR: spline.type should be \'POLY\', found: \'" + spline.type + "\'")

            else:
                #first write point count
                if export_options.write_bin == True:
                    pack_and_write_float((float(len(spline.points))), outputfile_bin)

                #now write all points
                for point in spline.points:

                    #transform to world coordinates
                    wcoords = scene_obj.matrix_world * point.co

                    #write as bin
                    if export_options.write_bin == True:
                        write_floats_binary(wcoords, point.tilt, outputfile_bin, export_options)
    
    except:
        #error handling
        print("ERROR: Failed to write Curve data to: " + filepath_bin)

    finally:
        #close file
        print("Closing file: " + filepath_bin)
        outputfile_bin.close()

def write_floats_binary(wcoords, tilt, file, export_options):
    #write point in world coordinates
    pack_and_write_float((float(wcoords.x)), file)
    pack_and_write_float((float(wcoords.y)), file)
    pack_and_write_float((float(wcoords.z)), file)

    #write tilt value
    if export_options.export_tilt == True:
        pack_and_write_float((float)(tilt), file)

def pack_and_write_float(float_data, file):
    # '>f' forces to write binary float in Big Endian format
    struct_out = struct.pack('>f', float_data)
    file.write(struct_out)

######################## ascii writer ######################

def write_ascii_file(scene_obj, filepath_txt, export_options):
    outputfile_txt = open(filepath_txt, 'w')

    try:
        for spline in scene_obj.data.splines:
            #only write POLY
            if spline.type != 'POLY':
                print("ERROR: spline.type should be \'POLY\', found: \'" + spline.type + "\'")

            else:
                #first write point count
                if export_options.write_ascii == True:
                    write_float((float(len(spline.points))), outputfile_txt)
                    outputfile_txt.write("\n")

                #now write all points
                for point in spline.points:

                    #transform to world coordinates
                    wcoords = scene_obj.matrix_world * point.co

                    #write as ascii
                    if export_options.write_ascii == True:
                        write_floats_ascii(wcoords, point.tilt, outputfile_txt, export_options)
        
    except:
        #error handling
        print("ERROR: Failed to write Curve data to: " + filepath_txt)

    finally:
        #close file
        print("Closing file: " + filepath_txt)
        outputfile_txt.close()

def write_floats_ascii(wcoords, tilt, file, export_options):
    #write point in world coordinates
    write_float((float(wcoords.x)), file)
    write_float((float(wcoords.y)), file)
    write_float((float(wcoords.z)), file)

    #write tilt value 
    if export_options.export_tilt == True:
        write_float((float(tilt)), file)
    
    #write newline
    file.write("\n")

def write_float(float_data, file):
    file.write("%.5f " % float_data)

######################## add-in functions #################

def register():
    bpy.utils.register_class(PathExporter)
    bpy.types.INFO_MT_file_export.append(menu_func);
    
def unregister():
    bpy.utils.unregister_class(PathExporter)
    bpy.types.INFO_MT_file_export.remove(menu_func);

if __name__ == "__main__":
    register()