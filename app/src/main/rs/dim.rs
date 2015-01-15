#pragma version(1)
#pragma rs java_package_name(com.medo.pravoslavenkalendar.services)


rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;

float factor = .4f;

uchar4 __attribute__((kernel)) dim(uchar4 in)
{
    uchar4 out = in;
    out.r = in.r * factor;
    out.g = in.g * factor;
    out.b = in.b * factor;
    return out;
}