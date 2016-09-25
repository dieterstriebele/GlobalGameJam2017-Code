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
from bpy.props import *
import os.path
import struct

######################## helper ###########################

class PathExportProperty(bpy.types.PropertyGroup):
    name = bpy.props.StringProperty(name="Export Option Name")
    export = bpy.props.BoolProperty(name="Export", description="Option Name", default=False)

######################## main #############################

class PathExporter(bpy.types.Operator, ExportHelper):
    """Export Animation Paths"""
    bl_idname   = "export.export_paths"
    bl_label    = "Export Animation Paths"
    bl_options  = {'REGISTER'}

    filename_ext = ".kbap"

    def execute(self, context):
        #get list of objects from scene
        object_list = list(bpy.data.objects)

        #get file path from UI
        filepath = self.filepath

        #IMPORTANT NOTE: Blender Curve Paths MUST be in 'POLY' mode!
        for scene_obj in object_list:
            if scene_obj.type == 'CURVE' and scene_obj.name[:5] == 'kbap_':
                print("CURVE found for export: " + scene_obj.name)
                write(scene_obj, filepath)                
        
        return {'FINISHED'}
        
def menu_func(self, context):
    self.layout.operator(PathExporter.bl_idname, text="Export Animation Paths (.kbap)");

######################## file writer ######################

def write(scene_obj, filepath):
    filepath_bin = filepath[:-5] + '_' + scene_obj.name + '.kbin'
    outputfile_bin = open(filepath_bin, 'wb')

    filepath_txt = filepath[:-5] + '_' + scene_obj.name + '.kbap'    
    outputfile_txt = open(filepath_txt, 'w')

    if scene_obj.type != 'CURVE':
        print("euhm... shouldn't EVER get here!")

    for spline in scene_obj.data.splines:
        if spline.type != 'POLY':
            print("error: spline.type should be \'POLY\', found: \'" + spline.type + "\'")
        else:
            #first write point count to bin and txt file
            pack_and_write_float((float(len(spline.points))), outputfile_bin)
            write_float((float(len(spline.points))), outputfile_txt)
            outputfile_txt.write("\n")

            #now write all points
            for point in spline.points:
                #transform to world coordinates
                wcoords = scene_obj.matrix_world * point.co
                #now write to bin and txt files
                write_floats_binary(wcoords, point.tilt, outputfile_bin)
                write_floats_text(wcoords, point.tilt, outputfile_txt)
                #output for logging
                print("%.5f, %.5f, %.5f, %.5f" % (wcoords.x, wcoords.y, wcoords.z, point.tilt))

    outputfile_bin.close()
    print("success! file written: " + filepath_bin)

    outputfile_txt.close()
    print("success! file written: " + filepath_txt)

######################## binary writer ####################

def write_floats_binary(wcoords, tilt, file):
    #write point in world coordinates
    pack_and_write_float((float(wcoords.x)), file)
    pack_and_write_float((float(wcoords.y)), file)
    pack_and_write_float((float(wcoords.z)), file)

    #write tilt value(s)
    if False:
        pack_and_write_float((float)(tilt), file)

def pack_and_write_float(float_data, file):
    # '>f' forces to write binary float in Big Endian format
    struct_out = struct.pack('>f', float_data)
    file.write(struct_out)

######################## txt writer ######################

def write_floats_text(wcoords, tilt, file):
    #write point in world coordinates
    write_float((float(wcoords.x)), file)
    write_float((float(wcoords.y)), file)
    write_float((float(wcoords.z)), file)

    #write tilt value(s)
    
    if False:
        write_float((float(tilt)), file)
    
    #write newline
    file.write("\n")

def write_float(float_data, file):
    file.write("%.5f " % float_data)

######################## add-in functions #################

def register():
    bpy.utils.register_class(PathExporter)
    bpy.utils.register_class(PathExportProperty)
    bpy.types.INFO_MT_file_export.append(menu_func);
    
def unregister():
    bpy.utils.unregister_class(PathExporter)
    bpy.utils.unregister_class(PathExportProperty)
    bpy.types.INFO_MT_file_export.remove(menu_func);

if __name__ == "__main__":
    register()