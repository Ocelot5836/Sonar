package io.github.ocelot.sonar.client.shader;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.NativeResource;

import java.nio.FloatBuffer;
import java.util.Map;
import java.util.OptionalInt;

import static org.lwjgl.opengl.GL11C.glGetInteger;
import static org.lwjgl.opengl.GL20C.*;

/**
 * <p>A usable instance of a {@link ShaderProgram}.</p>
 *
 * @author Ocelot
 * @since 7.0.0
 */
public class ShaderInstance implements NativeResource
{
    public static final int MAX_VERTEX_ATTRIBUTES = glGetInteger(GL_MAX_VERTEX_ATTRIBS);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final FloatBuffer MATRIX_4_4 = BufferUtils.createFloatBuffer(4 * 4);

    private final Map<CharSequence, Integer> uniforms;
    private int program;

    ShaderInstance(int program)
    {
        this.program = program;
        this.uniforms = new Object2IntArrayMap<>();
    }

    /**
     * Checks for a uniform with the specified name.
     *
     * @param uniformName The name of the uniform to fetch
     * @return An optional of the uniform with that name
     */
    public OptionalInt getUniform(CharSequence uniformName)
    {
        int uniform = this.uniforms.computeIfAbsent(uniformName, key ->
        {
            int location = glGetUniformLocation(this.program, uniformName);
            if (location == -1)
                LOGGER.warn("Unknown uniform: " + uniformName);
            return location;
        });
        return uniform == -1 ? OptionalInt.empty() : OptionalInt.of(uniform);
    }

    /**
     * Loads a single boolean into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param value       The boolean value to upload
     */
    public void loadBoolean(CharSequence uniformName, boolean value)
    {
        this.loadInt(uniformName, value ? 1 : 0);
    }

    /**
     * Loads two booleans into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param x           The first boolean value to upload
     * @param y           The second boolean value to upload
     */
    public void loadBooleans(CharSequence uniformName, boolean x, boolean y)
    {
        this.loadInts(uniformName, x ? 1 : 0, y ? 1 : 0);
    }

    /**
     * Loads three booleans into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param x           The first boolean value to upload
     * @param y           The second boolean value to upload
     * @param z           The third boolean value to upload
     */
    public void loadBooleans(CharSequence uniformName, boolean x, boolean y, boolean z)
    {
        this.loadInts(uniformName, x ? 1 : 0, y ? 1 : 0, z ? 1 : 0);
    }

    /**
     * Loads four booleans into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param x           The first boolean value to upload
     * @param y           The second boolean value to upload
     * @param z           The third boolean value to upload
     * @param w           The third boolean value to upload
     */
    public void loadBooleans(CharSequence uniformName, boolean x, boolean y, boolean z, boolean w)
    {
        this.loadInts(uniformName, x ? 1 : 0, y ? 1 : 0, z ? 1 : 0, w ? 1 : 0);
    }

    /**
     * Loads a single float into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param value       The float value to upload
     */
    public void loadFloat(CharSequence uniformName, float value)
    {
        this.getUniform(uniformName).ifPresent(uniform -> glUniform1f(uniform, value));
    }

    /**
     * Loads two floats into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param x           The first float value to upload
     * @param y           The second float value to upload
     */
    public void loadFloats(CharSequence uniformName, float x, float y)
    {
        this.getUniform(uniformName).ifPresent(uniform -> glUniform2f(uniform, x, y));
    }

    /**
     * Loads three floats into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param x           The first float value to upload
     * @param y           The second float value to upload
     * @param z           The third float value to upload
     */
    public void loadFloats(CharSequence uniformName, float x, float y, float z)
    {
        this.getUniform(uniformName).ifPresent(uniform -> glUniform3f(uniform, x, y, z));
    }

    /**
     * Loads a single integer into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param value       The integer value to upload
     */
    public void loadInt(CharSequence uniformName, int value)
    {
        this.getUniform(uniformName).ifPresent(uniform -> glUniform1i(uniform, value));
    }

    /**
     * Loads two integers into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param x           The first integer value to upload
     * @param y           The second integer value to upload
     */
    public void loadInts(CharSequence uniformName, int x, int y)
    {
        this.getUniform(uniformName).ifPresent(uniform -> glUniform2i(uniform, x, y));
    }

    /**
     * Loads three integers into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param x           The first integer value to upload
     * @param y           The second integer value to upload
     * @param z           The third  integer value to upload
     */
    public void loadInts(CharSequence uniformName, int x, int y, int z)
    {
        this.getUniform(uniformName).ifPresent(uniform -> glUniform3i(uniform, x, y, z));
    }

    /**
     * Loads four integers into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param x           The first integer value to upload
     * @param y           The second integer value to upload
     * @param z           The third  integer value to upload
     * @param w           The fourth  integer value to upload
     */
    public void loadInts(CharSequence uniformName, int x, int y, int z, int w)
    {
        this.getUniform(uniformName).ifPresent(uniform -> glUniform4i(uniform, x, y, z, w));
    }

    /**
     * Loads four floats into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param x           The first float value to upload
     * @param y           The second float value to upload
     * @param z           The third float value to upload
     * @param w           The fourth float value to upload
     */
    public void loadFloats(CharSequence uniformName, float x, float y, float z, float w)
    {
        this.getUniform(uniformName).ifPresent(uniform -> glUniform4f(uniform, x, y, z, w));
    }

    /**
     * Loads a 3D vector into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param value       The vector value to upload
     */
    public void loadVector(CharSequence uniformName, Vector3f value)
    {
        this.loadFloats(uniformName, value.x(), value.y(), value.z());
    }

    /**
     * Loads a 4D vector into the shader
     *
     * @param uniformName The name of the uniform to load
     * @param value       The vector value to upload
     */
    public void loadVector(CharSequence uniformName, Vector4f value)
    {
        this.loadFloats(uniformName, value.x(), value.y(), value.z(), value.w());
    }

    /**
     * Loads a 4x4 matrix into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param matrix      The matrix data to upload
     */
    public void loadMatrix(CharSequence uniformName, Matrix4f matrix)
    {
        this.getUniform(uniformName).ifPresent(uniform ->
        {
            matrix.store(MATRIX_4_4);
            glUniformMatrix4fv(uniform, false, MATRIX_4_4);
        });
    }

    /**
     * Binds this shader for using with future render calls.
     */
    public void bind()
    {
        if (this.program <= 0)
            return;
        glUseProgram(this.program);
    }

    /**
     * Unbinds the current shader and sets it back to the compatibility pipeline.
     */
    public static void unbind()
    {
        glUseProgram(0);
    }

    @Override
    public void free()
    {
        if (this.program == 0)
            return;
        if (this.program > 0)
            glDeleteProgram(this.program);
        this.setProgram(0);
    }

    void setProgram(int program)
    {
        this.program = program;
        this.uniforms.clear();
    }

    /**
     * @return The OpenGL id of the program
     */
    public int getProgram()
    {
        return program;
    }
}
