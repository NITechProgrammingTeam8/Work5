using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Operating : MonoBehaviour
{

    private Vector3 velocity;
    private float moveSpeed = 1000.0f;

    void Update()
    {
        velocity = Vector3.zero;
        if (Input.GetKey(KeyCode.W))
            velocity.z += 1;
        if (Input.GetKey(KeyCode.A))
            velocity.x -= 1;
        if (Input.GetKey(KeyCode.S))
            velocity.z -= 1;
        if (Input.GetKey(KeyCode.D))
            velocity.x += 1;
        if (Input.GetKey(KeyCode.E))
            velocity.y += 1;
        if (Input.GetKey(KeyCode.Q))
            velocity.y -= 1;
        // 速度ベクトルの長さを1秒でmoveSpeedだけ進むように調整します
        velocity = velocity.normalized * moveSpeed * Time.deltaTime;

        if (velocity.magnitude > 0)
        {
            // プレイヤーの位置(transform.position)の更新
            // 移動方向ベクトル(velocity)を足し込みます
            GameObject go = Clicked.clickedGameObject;
            if (go != null)
            {
                go.GetComponent<Rigidbody>().AddForce(velocity);
            }
        }
    }
}
